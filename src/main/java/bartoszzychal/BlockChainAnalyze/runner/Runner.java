package bartoszzychal.BlockChainAnalyze.runner;

import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.log4j.Logger;
import org.bitcoinj.core.Block;
import org.bitcoinj.core.Sha256Hash;
import org.bitcoinj.core.Transaction;
import org.bitcoinj.params.MainNetParams;

import bartoszzychal.BlockChainAnalyze.blockchainreader.impl.BlockChainIndexDatabaseReader;
import bartoszzychal.BlockChainAnalyze.blockfileloader.BlockLoader;
import bartoszzychal.BlockChainAnalyze.dbconnection.IBitCoinIndexRepository;
import bartoszzychal.BlockChainAnalyze.dbconnection.hsql.HsqlBitcoinIndexRepository;
import bartoszzychal.BlockChainAnalyze.fileloader.FileLoader;
import bartoszzychal.BlockChainAnalyze.finder.BlocksConnectionFinder;
import bartoszzychal.BlockChainAnalyze.index.persistance.BlockIndex;
import bartoszzychal.BlockChainAnalyze.model.TransactionConnectionInput;
import bartoszzychal.BlockChainAnalyze.model.TransactionConnectionOutput;
import bartoszzychal.BlockChainAnalyze.utils.Utils;

public class Runner extends AbstractRunner {
	private static final Logger log = Logger.getLogger(Runner.class);

	private Sha256Hash[] startBlockHash = {
			Sha256Hash.wrap("00000000000000000033b23b4895616a1be85c364ed06b0aa6591ceee4f87328"), //2017-06-30
			Sha256Hash.wrap("000000000000000001dd9607859bb7a367a0db89f0a0dc8cb7ced9964223bed1"), //2017-03-31
			Sha256Hash.wrap("000000000000000003458396c743cdfa8247a4f6bc28ad413dba333a436edf75"), //2016-12-31
			Sha256Hash.wrap("000000000000000000ff7e9b59f8f880e07bfaed552f62c4a27cb869dfdda8cf"), //2016-09-30
			Sha256Hash.wrap("000000000000000002024ff8a7991edec3b7a58575fb6563b0bf4cad2e4106fc"), //2016-06-30
			Sha256Hash.wrap("000000000000000003532be135e917df7d1d5c7deff5ffc16d346f6a8b9f323c"), //2016-03-31
			Sha256Hash.wrap("000000000000000002dfcd5cd05cd4f80d792e51ecdc5942cd6cec1365b22a2d"), //2015-12-31
			Sha256Hash.wrap("000000000000000005fc45403dda82efeee0beaa4c4dacaa6af63f23c5947b92"), //2015-09-30
			Sha256Hash.wrap("0000000000000000136329764b75ae34a79d2bafeab4cd2c60c077ce5c7aec25"), //2015-06-30
			Sha256Hash.wrap("00000000000000000bca1b1e8c5fdad63b1c23dd7bb7e0bb37bbcf8039210fc3") //2015-03-31
	};
	
	

	public static void main(String[] args) {
		Long connectionsLimit = Long.valueOf(100);
		final Runner runner = new Runner();
		runner.run(connectionsLimit, 2);
	}

	private void run(Long connectionsLimit, Integer sampleStart) {
		FileLoader.setDir("D:/PWR/mgr/Praca Magisterska/BitCoinCore/BitcoinCoreInstall/blocks/");
		final IBitCoinIndexRepository repository = new HsqlBitcoinIndexRepository();
		final BlockLoader blockLoader = new BlockLoader(MainNetParams.get(), FileLoader.readFiles());
		final BlockChainIndexDatabaseReader blockchainReader = new BlockChainIndexDatabaseReader(repository);
		final BlocksConnectionFinder blocksConnectionFinder = new BlocksConnectionFinder();
		int startHash = 0;
		int range = startBlockHash.length;
		
		if (sampleStart != null) {
			startHash = sampleStart.intValue();
			range = startHash + 1;
		}
		
		for (int i = startHash; i < range; i++) {
			final int blockNumber = i;
			final BlockIndex blockIndex = repository.readIndex(startBlockHash[blockNumber].toString());
			final Block block = blockLoader.readBlock(blockIndex);
			log.info("\nB" + (blockNumber + 1) + ": "  + block.getHashAsString());
			List<Transaction> transactions = block.getTransactions();
			log.info("\nDate: " + Utils.parse(block.getTimeSeconds()).toString());
			if (CollectionUtils.isNotEmpty(transactions) && transactions.size() >= 11) {
				//get The 10 transaction with the biggest count of Inputs without coinbase.
//				transactions = transactions
//						.stream()
//						.filter(t -> !t.isCoinBase())
//						.sorted((t1, t2) -> Long.valueOf(t2.getInputs().size()).compareTo(Long.valueOf(t1.getInputs().size())))
//						.collect(Collectors.toList());
				
				// First is always coiBase.
				int skip = 1;
				for (int j = skip; j < skip + 11; j++) {
					final int transactionNumber = j;
					// get transaction
					final Transaction transaction = transactions.get(transactionNumber);
					// get hash
					log.info("Count of inputs: " + transaction.getInputs().size());
					final Sha256Hash transactionHash = transaction.getHash();
					// generate Input
					final TransactionConnectionInput transactionConnectionInput = new TransactionConnectionInput(
							Utils.parse(block.getTimeSeconds()).toLocalDate(), startBlockHash[blockNumber],
							transactionHash, connectionsLimit);
					log.info("T" + transactionNumber + ":" + transactionConnectionInput.getStartTransactionHash().toString());
					// start finding the connections
					final TransactionConnectionOutput tc = blocksConnectionFinder
							.findConnections(transactionConnectionInput, blockchainReader);
					// write result to filke
					if (tc.getConnectionsFoundSuccess()) {
						writeToFile(tc.getConnections(), connectionsLimit.longValue(), blockNumber, transactionNumber, transactionHash);
					} else {
						log.warn("TransactionInput probably create from CoinBase. Transaction " + transactionHash + " skip.");
						skip++;
						if (transactions.size() >= skip + 11) {
							break;
						}
					}
				}
			}

		}
		// close connection with DB
		repository.closeConnection();
	}
}
