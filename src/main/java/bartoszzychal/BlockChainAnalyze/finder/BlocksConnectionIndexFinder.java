package bartoszzychal.BlockChainAnalyze.finder;

import java.text.DecimalFormat;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;
import org.bitcoinj.core.Sha256Hash;

import bartoszzychal.BlockChainAnalyze.blockchainreader.IBlockChainIndexReader;
import bartoszzychal.BlockChainAnalyze.generator.TransactionSearchInfoGenerator;
import bartoszzychal.BlockChainAnalyze.index.persistance.BlockIndex;
import bartoszzychal.BlockChainAnalyze.index.persistance.Transaction;
import bartoszzychal.BlockChainAnalyze.model.TransactionConnection;
import bartoszzychal.BlockChainAnalyze.model.TransactionConnectionInput;
import bartoszzychal.BlockChainAnalyze.model.TransactionConnectionOutput;
import bartoszzychal.BlockChainAnalyze.model.TransactionSearchInfo;

public class BlocksConnectionIndexFinder {

    private static final Logger log = Logger.getLogger(BlocksConnectionIndexFinder.class);

	public BlocksConnectionIndexFinder() {

	}

	public TransactionConnectionOutput findConnections(final TransactionConnectionInput transactionConnectionInput,
			final IBlockChainIndexReader blockchainReader) {
		
		if (blockchainReader == null) {
			throw new IllegalArgumentException("BlockchainReader == null");
		}
		
		TransactionConnectionOutput transactionConnectionOutput = null;
		final long limit = transactionConnectionInput.getConnectionsLimit();
		final Sha256Hash startBlockHash = transactionConnectionInput.getStartBlockHash();
		final Sha256Hash startTransactionHash = transactionConnectionInput.getStartTransactionHash();
		final LocalDate startDate = transactionConnectionInput.getStartDate();

		LocalDate to = LocalDate.now();
		if (startDate != null) {
			to = startDate;
		}
		log.info("Start find with date " + to);

		final LocalDateTime startTime = LocalDateTime.now();
		
		final Set<TransactionSearchInfo> connectionsToFind = new HashSet<>();
		final Set<TransactionConnection> foundConnection = new HashSet<>();
		long counter = 0;
		
		final BlockIndex startIndex = blockchainReader.readIndex(startBlockHash.toString());

		if (startIndex != null) {
			if (startTransactionHash != null) {
				// Find the transaction
				final Transaction startTransaction = startIndex.getTransactions().stream()
						.filter(t -> t.getTransactionHash().equals(startTransactionHash)).findFirst().orElseGet(null);
				if (startTransaction != null) {
					log.info("Start find with TransactionHash " + startTransactionHash);
					connectionsToFind.add(TransactionSearchInfoGenerator.generate(startTransaction, startIndex));
				}
			} else {
				connectionsToFind.addAll(
						startIndex.getTransactions().stream().map(t -> TransactionSearchInfoGenerator.generate(t, startIndex))
						.collect(Collectors.toList()));
			}
			// Map the Transaction to TransactionConnection's. It
			// will be use as input to find connected transaction.
			
		}
		
		while (counter < limit) {
			if (counter >= limit) break;
			List<BlockIndex> blocks = blockchainReader.readBlockIndexFromTo(to, to, new ArrayList<>(connectionsToFind));
			for (BlockIndex block : blocks) {
				if (counter >= limit) break;
				// Get all Transactions from this Block.
				final Set<Transaction> transactions = block.getTransactions();
				final Set<TransactionSearchInfo> newConnectionsToFind = new HashSet<>();
				// List new connections to find. List will be added to
				// connections to find in next Block.
				int checkedTransactionsConnectionCounter = 0;
				for (TransactionSearchInfo connectionInfo : connectionsToFind) {
					if (counter >= limit) break;
					for (Transaction transaction : transactions) {
						if (counter >= limit) break;
						// Check if at least one input transaction is connected
						// to output the transaction from this block.
						TransactionConnection tc = TransactionChecker.areTransactionsConnected(connectionInfo, transaction, block);
						// If is connected:
						if (tc.isConnected()) {
							// Increase the counter
							counter++;
							// add Connection to found connection
							foundConnection.add(tc);
							// create new Connection to found from this
							// Transaction Inputs.
							final TransactionSearchInfo newConnectionToFind = TransactionSearchInfoGenerator.generate(transaction, block);
							// add to List new connections to find.
							newConnectionsToFind.add(newConnectionToFind);
							
						}
					}
					checkedTransactionsConnectionCounter++;
					double progress = (double) ((double)checkedTransactionsConnectionCounter / (double)connectionsToFind.size() * 100);
				    DecimalFormat df = new DecimalFormat();
				    df.setMaximumFractionDigits(4);
				    df.setMinimumFractionDigits(4);
					log.info("Checked [" +df.format(progress) + "%] Transaction Connections.");
				}
				log.info("In Block " + block.getBlockHash());
				log.info("Count of all found connections in block: " + foundConnection.size());
				// remove found connection
				log.info("Count of new connections to find from block: " + newConnectionsToFind.size());
				// add new connections to find
				connectionsToFind.addAll(newConnectionsToFind);
				// all 
				log.info("Count of all connections to find: " + connectionsToFind.size());
				final LocalDateTime logTime = LocalDateTime.now();
				final Duration duration = Duration.between(startTime, logTime);
				log.info("Work time: " + duration.get(ChronoUnit.SECONDS));
			}
			to = to.minusDays(1);
			if (to.isBefore(LocalDate.of(2009, 1, 8))) {
				break;
			}
		}
		transactionConnectionOutput = new TransactionConnectionOutput(transactionConnectionInput,
				new ArrayList<TransactionConnection>(foundConnection));

		return transactionConnectionOutput;
	}

}
