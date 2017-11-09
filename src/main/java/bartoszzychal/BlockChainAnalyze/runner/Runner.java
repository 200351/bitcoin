package bartoszzychal.BlockChainAnalyze.runner;

import java.time.LocalDate;

import org.bitcoinj.core.Sha256Hash;

import bartoszzychal.BlockChainAnalyze.BlocksConnectionFinder;
import bartoszzychal.BlockChainAnalyze.blockchainreader.impl.BlockChainIndexDatabaseReader;
import bartoszzychal.BlockChainAnalyze.dbconnection.IBitCoinIndexRepository;
import bartoszzychal.BlockChainAnalyze.dbconnection.hsql.HsqlBitcoinIndexRepository;
import bartoszzychal.BlockChainAnalyze.fileloader.FileLoader;
import bartoszzychal.BlockChainAnalyze.model.TransactionConnectionInput;
import bartoszzychal.BlockChainAnalyze.model.TransactionConnectionOutput;

public class Runner extends AbstractRunner {

	private LocalDate[] startDate = { 
			LocalDate.of(2017, 06, 30) 
	};
	
	private Sha256Hash[] startBlockHash = {
			Sha256Hash.wrap("00000000000000000033b23b4895616a1be85c364ed06b0aa6591ceee4f87328")
	};
	
	private Sha256Hash[] startTransactionHash = {
			Sha256Hash.wrap("d849f49113b0c1dddb42b40f4d25ff080fcf633f8a24ce7243eaee2288b18624") 
	};

	public static void main(String[] args) {
		int connectionsLimit = 100000;
		final Runner runner = new Runner();
		runner.run(connectionsLimit);
	}

	private void run(int connectionsLimit) {

		for (int i = 0; i < startBlockHash.length; i++) {
			FileLoader.setDir("D:/PWR/mgr/Praca Magisterska/BitCoinCore/BitcoinCoreInstall/blocks_t/" + (i + 1));
			final BlocksConnectionFinder blocksConnectionFinder = new BlocksConnectionFinder();
			TransactionConnectionInput transactionConnectionInput = new TransactionConnectionInput(startDate[i],
					startBlockHash[i], startTransactionHash[i], connectionsLimit);
			final IBitCoinIndexRepository repository = new HsqlBitcoinIndexRepository();
			final BlockChainIndexDatabaseReader blockchainReader = new BlockChainIndexDatabaseReader(repository );
			final TransactionConnectionOutput tc = blocksConnectionFinder.findConnections(transactionConnectionInput, blockchainReader);
			writeToFile(tc.getConnections(), connectionsLimit, i, startTransactionHash[i]);
		}

	}
}
