package bartoszzychal.BlockChainAnalyze.finder;

import java.io.File;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.SetUtils;
import org.apache.log4j.Logger;
import org.bitcoinj.core.Block;
import org.bitcoinj.core.Context;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.core.Sha256Hash;
import org.bitcoinj.core.Transaction;
import org.bitcoinj.core.TransactionOutput;
import org.bitcoinj.params.MainNetParams;

import bartoszzychal.BlockChainAnalyze.blockchainreader.IBlockChainReader;
import bartoszzychal.BlockChainAnalyze.mapper.ConnectionFinderInputMapper;
import bartoszzychal.BlockChainAnalyze.mapper.InfoMapper;
import bartoszzychal.BlockChainAnalyze.model.ConnectionFinderInput;
import bartoszzychal.BlockChainAnalyze.model.ConnectionFinderOutput;
import bartoszzychal.BlockChainAnalyze.model.OutputInfo;
import bartoszzychal.BlockChainAnalyze.model.TransactionConnectionInput;
import bartoszzychal.BlockChainAnalyze.model.TransactionConnectionOutput;
import bartoszzychal.BlockChainAnalyze.utils.Utils;

public class BlocksConnectionFinder implements Runnable {

    private static final Logger log = Logger.getLogger(BlocksConnectionFinder.class);
	private TransactionConnectionInput transactionConnectionInput;
	private IBlockChainReader blockchainReader;
	private Integer statisticsBlocks;
	public BlocksConnectionFinder() {
	}
	public BlocksConnectionFinder(TransactionConnectionInput transactionConnectionInput, IBlockChainReader blockchainReader, Integer statisticsBlocks) {
		this.transactionConnectionInput = transactionConnectionInput;
		this.blockchainReader = blockchainReader;
		this.statisticsBlocks = statisticsBlocks;
	}
	
	public TransactionConnectionOutput findConnections(final TransactionConnectionInput transactionConnectionInput,
			final IBlockChainReader blockchainReader, Integer statisticsBlocks) {
		
		if (blockchainReader == null) {
			throw new IllegalArgumentException("BlockchainReader == null");
		}
		
		TransactionConnectionOutput transactionConnectionOutput = null;
		final long limit = transactionConnectionInput.getConnectionsLimit();
		final Sha256Hash startBlockHash = transactionConnectionInput.getStartBlockHash();
		final List<Sha256Hash> startTransactionHash = transactionConnectionInput.getStartTransactionHash();

		final Block startBlock = blockchainReader.readBlock(startBlockHash.toString());
		List<ConnectionFinderInput> connectionFindInputs = new ArrayList<>();
		if (startBlock != null && CollectionUtils.isNotEmpty(startBlock.getTransactions())) {
			Stream<Transaction> transactionStream = startBlock.getTransactions().stream().filter(t -> !t.isCoinBase());
			 if (CollectionUtils.isNotEmpty(startTransactionHash)) {
				 transactionStream = transactionStream.filter(t -> startTransactionHash.contains(t.getHash()));
			 }
			 connectionFindInputs = transactionStream.map(t -> ConnectionFinderInputMapper.map(t, startBlock, limit)).collect(Collectors.toList());
			 prepareFiles(transactionConnectionInput.getOutputDirectory(), connectionFindInputs);
		}

		int usedBlocksCount = 0;
		List<Block> blocks;
		LocalDateTime to = Utils.parse(startBlock.getTimeSeconds());
		while (transactionConnectionOutput == null && (blocks = blockchainReader.readBlockChainFromTo(to.toLocalDate(), to.toLocalDate())) != null) {
			for (Block block : blocks) {
				if (transactionConnectionOutput != null) {
					break;
				}
				usedBlocksCount++;
				// List new connections to find. List will be added to
				List<Transaction> transactions = block.getTransactions();
//				int checkedTransactionsCounter = 0;
				for (Transaction transaction : transactions) {
					final LocalDateTime startTime = LocalDateTime.now();
					List<TransactionOutput> outputs = transaction.getOutputs();
					final Set<OutputInfo> outputsInfo = outputs != null
							? outputs.stream().map(InfoMapper::map).filter(Utils.isNotNull()).collect(Collectors.toSet())
									: SetUtils.EMPTY_SORTED_SET;
					if (CollectionUtils.isNotEmpty(outputsInfo)) {
						final List<Thread> threads = runFinders(connectionFindInputs, outputsInfo, block, transaction, limit);
						Utils.waitForAllThreads(threads);
//						checkedTransactionsCounter++;
//						double progress = (double) ((double)checkedTransactionsCounter / (double)transactions.size() * 100);
//						DecimalFormat df = new DecimalFormat();
//						df.setMaximumFractionDigits(4);
//						df.setMinimumFractionDigits(4);
//						log.info("Checked [" +df.format(progress) + "%] Transaction of Block: " +  block.getHashAsString());
						
						if (CollectionUtils.isEmpty(threads)) {
							transactionConnectionOutput = prepareOutput(transactionConnectionInput, connectionFindInputs);
							break;
						}
					}
//					final Duration duration = Duration.between(startTime, LocalDateTime.now());
//					log.info("Work time: " + duration.get(ChronoUnit.SECONDS));
				}
				log.info(Thread.currentThread().getName() + " Used blocks: " + usedBlocksCount);
				if (statisticsBlocks != null && usedBlocksCount >= statisticsBlocks.intValue()) {
					log.info("Generate result for statistics.");
					transactionConnectionOutput = transactionConnectionOutput != null ? transactionConnectionOutput
							: prepareOutput(transactionConnectionInput, connectionFindInputs);
				}
			}
			to = to.minusDays(1);
			if (to.toLocalDate().isBefore(LocalDate.of(2009, 1, 8))) {
				break;
			}
		}
		return transactionConnectionOutput;
	}

	private void prepareFiles(String outputDirectory, List<ConnectionFinderInput> connectionFindInputs) {
		if (outputDirectory != null) {
			for (int i = 0; i < connectionFindInputs.size(); i++) {
				ConnectionFinderInput connectionFinderInput = connectionFindInputs.get(i);
				String blockHash = connectionFinderInput.getStartBlock().getHashAsString();
				String transactionHash = connectionFinderInput.getStartTransaction().getHashAsString();
				final File coinsFile = new File(
						outputDirectory + "\\coins" + "\\" + i + "_"+ blockHash + "_" + transactionHash + ".csv");
				final File timeFile = new File(
						outputDirectory + "\\time" + "\\"+ i + "_" + blockHash + "_" + transactionHash + ".csv");
				final File fullFile = new File(
						outputDirectory + "\\full" + "\\" + i + "_" + blockHash + "_" + transactionHash + ".csv");
				connectionFinderInput.setCoinsFileOutput(coinsFile);
				connectionFinderInput.setTimeFileOutput(timeFile);
				connectionFinderInput.setFullFileOutput(fullFile);
			}
		 }
	}

	private TransactionConnectionOutput prepareOutput(TransactionConnectionInput input, List<ConnectionFinderInput> connectionFindInputs) {
		final List<ConnectionFinderOutput> connectioFinders = connectionFindInputs.stream().map(cfi-> {
			final ConnectionFinderOutput connectionFinderOutput = new ConnectionFinderOutput();
			cfi.closeFiles();
			connectionFinderOutput.setStartBlock(cfi.getStartBlock());
			connectionFinderOutput.setStartTransaction(cfi.getStartTransaction());
			connectionFinderOutput.setFoundConnection(cfi.getFoundConnection());
			connectionFinderOutput.setSuccess(cfi.getFoundConnection().size() >= input.getConnectionsLimit());
			return connectionFinderOutput;
		}).collect(Collectors.toList());
		return new TransactionConnectionOutput(input, connectioFinders);
	}

	private List<Thread> runFinders(List<ConnectionFinderInput> connectionFindInputs,Set<OutputInfo> outputsInfo, Block block, Transaction transaction, long limit) {
		// connections to find in next Block.
		// prepare all possible output addresses from TransactionsOutputs
		final List<ConnectionFinderInput> connectionFinderInputs = connectionFindInputs.stream()
				.filter(c -> !c.isLimitObtained()).collect(Collectors.toList());
		final List<Thread> threads = new ArrayList<>(connectionFinderInputs.size());
		for (ConnectionFinderInput connectionFindInput : connectionFinderInputs) {
			connectionFindInput.setOutputsInfo(new HashSet<>(outputsInfo));
			connectionFindInput.setBlock(block);
			connectionFindInput.setTransaction(transaction);
			final ConnectionFinder connectionFinder = new ConnectionFinder(connectionFindInput);
			final Thread thread = new Thread(connectionFinder);
			threads.add(thread);
			thread.start();
		}
		return threads;
	}
	@Override
	public void run() {
		findConnections(transactionConnectionInput, blockchainReader, statisticsBlocks);
	}

}
