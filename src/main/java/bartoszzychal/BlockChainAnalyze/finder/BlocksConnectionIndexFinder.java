package bartoszzychal.BlockChainAnalyze.finder;

import java.text.DecimalFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
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
		final Sha256Hash startTransactionHash = CollectionUtils
				.isNotEmpty(transactionConnectionInput.getStartTransactionHash())
						? transactionConnectionInput.getStartTransactionHash().get(0) : null;

		final LocalDateTime startTime = LocalDateTime.now();
		
		final Set<TransactionSearchInfo> connectionsToFind = new HashSet<>();
		final Set<TransactionConnection> foundConnection = new HashSet<>();
		long counter = 0;
		
		final BlockIndex startIndex = blockchainReader.readIndex(startBlockHash.toString());
		LocalDateTime to = startIndex.getGeneratedDate();
		LocalDateTime from = startIndex.getGeneratedDate().minusHours(1);
		log.info("Start find with date " + to);

		
		if (startIndex != null) {
			if (startTransactionHash != null) {
				// Find the transaction
				Transaction startTransaction = null;
				if (CollectionUtils.isNotEmpty(startIndex.getTransactions())) {
					 startTransaction = startIndex.getTransactions().stream()
							.filter(t -> startTransactionHash.toString().equals(t.getTransactionHash())).findFirst().get();
				}
				if (startTransaction != null) {
					log.info("Start find with TransactionHash " + startTransactionHash);
					connectionsToFind.add(TransactionSearchInfoGenerator.generate(startTransaction));
				}
			} else {
				connectionsToFind.addAll(
						startIndex.getTransactions().stream().map(t -> TransactionSearchInfoGenerator.generate(t))
						.collect(Collectors.toList()));
			}
			// Map the Transaction to TransactionConnection's. It
			// will be use as input to find connected transaction.
			
		}
		
		while (foundConnection.size() < limit) {
			if (foundConnection.size() >= limit) break;
			List<Transaction> transactions = blockchainReader.readTransactionsFromTo(from, to, new ArrayList<>(connectionsToFind));
			final Set<TransactionSearchInfo> newConnectionsToFind = new HashSet<>();
			if (CollectionUtils.isNotEmpty(transactions)) {
				transactions.parallelStream().forEach(t -> {
					int checkedTransactionsConnectionCounter = 0;
					connectionsToFind.parallelStream().forEach(ctf -> {
						// Check if at least one input transaction is connected
						// to output the transaction from this block.
						TransactionConnection tc = TransactionChecker.areTransactionsConnected(ctf,
								t);
						// If is connected:
						if (tc.isConnected()) {
							// add Connection to found connection
							foundConnection.add(tc);
							// create new Connection to found from this
							// Transaction Inputs.
							final TransactionSearchInfo newConnectionToFind = TransactionSearchInfoGenerator
									.generate(t);
							// add to List new connections to find.
							newConnectionsToFind.add(newConnectionToFind);

						}
					});
					checkedTransactionsConnectionCounter++;
					double progress = (double) ((double)checkedTransactionsConnectionCounter / (double)connectionsToFind.size() * 100);
					DecimalFormat df = new DecimalFormat();
					df.setMaximumFractionDigits(4);
					df.setMinimumFractionDigits(4);
					log.info("Checked [" +df.format(progress) + "%] Transaction Connections.");
				});
//				for (Transaction transaction : transactions) {
//					if (counter >= limit) break;
//					for (TransactionSearchInfo connectionInfo : connectionsToFind) {
//						if (counter >= limit) break;
//						// Check if at least one input transaction is connected
//						// to output the transaction from this block.
//						TransactionConnection tc = TransactionChecker.areTransactionsConnected(connectionInfo,
//								transaction);
//						// If is connected:
//						if (tc.isConnected()) {
//							// Increase the counter
//							counter++;
//							// add Connection to found connection
//							foundConnection.add(tc);
//							// create new Connection to found from this
//							// Transaction Inputs.
//							final TransactionSearchInfo newConnectionToFind = TransactionSearchInfoGenerator
//									.generate(transaction);
//							// add to List new connections to find.
//							newConnectionsToFind.add(newConnectionToFind);
//
//						}
//					}
//				}
			}
			log.info("Count of all found connections: " + foundConnection.size());
			// remove found connection
			log.info("Count of new connections to find: " + newConnectionsToFind.size());
			// add new connections to find
			connectionsToFind.addAll(newConnectionsToFind);
			// all 
			log.info("Count of all connections to find: " + connectionsToFind.size());
			final LocalDateTime logTime = LocalDateTime.now();
			final Duration duration = Duration.between(startTime, logTime);
			log.info("Work time: " + duration.get(ChronoUnit.SECONDS));
			to = to.minusHours(1);
			from = to.minusHours(1);
			log.info("Input Date from " + from + " to "  + to);
			if (to.isBefore(LocalDateTime.of(2009, 1, 9, 0, 0))) {
				break;
			}
		}
		transactionConnectionOutput = new TransactionConnectionOutput(transactionConnectionInput,
				new ArrayList<TransactionConnection>(foundConnection));

		return transactionConnectionOutput;
	}

}
