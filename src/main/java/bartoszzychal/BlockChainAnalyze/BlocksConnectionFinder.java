package bartoszzychal.BlockChainAnalyze;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.bitcoinj.core.Block;
import org.bitcoinj.core.Sha256Hash;
import org.bitcoinj.core.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bartoszzychal.BlockChainAnalyze.blockchainreader.IBlockChainReader;
import bartoszzychal.BlockChainAnalyze.generator.TransactionSearchInfoGenerator;
import bartoszzychal.BlockChainAnalyze.model.TransactionConnection;
import bartoszzychal.BlockChainAnalyze.model.TransactionConnectionInput;
import bartoszzychal.BlockChainAnalyze.model.TransactionConnectionOutput;
import bartoszzychal.BlockChainAnalyze.model.TransactionSearchInfo;

public class BlocksConnectionFinder {

    private static final Logger log = LoggerFactory.getLogger(BlocksConnectionFinder.class);

	public BlocksConnectionFinder() {

	}

	public TransactionConnectionOutput findConnections(final TransactionConnectionInput transactionConnectionInput,
			final IBlockChainReader blockchainReader) {
		
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
		boolean startBlockHashFound = false;
		
		while (counter < limit) {
			final List<Block> blocks = blockchainReader.readBlockChainFromTo(to, to);
			for (Block block : blocks) {
				if (counter >= limit) break;
				// Find the start block.
				if (!startBlockHashFound) {
					if (block.getHash().equals(startBlockHash)) {
						log.info("Start find with BlockHash " + startBlockHash);
						startBlockHashFound = true;
						// Get all Transactions from this Block.
						final List<Transaction> transactions = block.getTransactions();
						// If it's setted start Transaction Hash;
						if (startTransactionHash != null) {
							// Find the transaction
							final Transaction startTransaction = transactions.stream()
									.filter(t -> t.getHash().equals(startTransactionHash)).findFirst().orElseGet(null);
							if (startTransaction != null) {
								log.info("Start find with TransactionHash " + startTransactionHash);
								final boolean coinBase = startTransaction.getInputs().stream()
										.anyMatch(t -> Sha256Hash.ZERO_HASH.equals(t.getOutpoint().getHash()));
								if (coinBase) throw new IllegalArgumentException("Start Transaction can not be a coinbase Transaction.");
								connectionsToFind.add(TransactionSearchInfoGenerator.generate(startTransaction, block));
								continue;
							}
						}
						// Map the Transaction to TransactionConnection's. It
						// will be use as input to find connected transaction.
						connectionsToFind.addAll(
								transactions.stream().map(t -> TransactionSearchInfoGenerator.generate(t, block))
										.collect(Collectors.toList()));
						// Continue because Block is not is no longer needed.
						continue;
					} else {
						// Continue because BlockHash is not equals as started
						// HashBlock.
						continue;
					}
				}

				// Get all Transactions from this Block.
				final List<Transaction> transactions = block.getTransactions();
				final Set<TransactionSearchInfo> newConnectionsToFind = new HashSet<>();
				// List new connections to find. List will be added to
				// connections to find in next Block.
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
				}
				log.info("In Block " + block.getHashAsString());
				log.info("Count of all found connections: " + foundConnection.size());
				// remove found connection
				log.info("Count of new connections to find: " + newConnectionsToFind.size());
				// add new connections to find
				connectionsToFind.addAll(newConnectionsToFind);
				final LocalDateTime logTime = LocalDateTime.now();
				final Duration duration = Duration.between(startTime, logTime);
				log.info("Work time: " + duration.get(ChronoUnit.SECONDS));
			}
			to = to.minusDays(1);
		}
		transactionConnectionOutput = new TransactionConnectionOutput(transactionConnectionInput,
				new ArrayList<TransactionConnection>(foundConnection));

		return transactionConnectionOutput;
	}

}
