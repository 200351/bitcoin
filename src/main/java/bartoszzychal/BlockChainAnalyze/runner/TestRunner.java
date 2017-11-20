package bartoszzychal.BlockChainAnalyze.runner;

import java.time.LocalDate;
import java.util.ArrayList;

import org.bitcoinj.core.Sha256Hash;

import bartoszzychal.BlockChainAnalyze.blockchainreader.impl.BlockChainByIndexReader;
import bartoszzychal.BlockChainAnalyze.dbconnection.IBitCoinIndexRepository;
import bartoszzychal.BlockChainAnalyze.dbconnection.hsql.HsqlBitcoinIndexRepository;
import bartoszzychal.BlockChainAnalyze.fileloader.FileLoader;
import bartoszzychal.BlockChainAnalyze.finder.BlocksConnectionFinder;
import bartoszzychal.BlockChainAnalyze.model.TransactionConnectionInput;
import bartoszzychal.BlockChainAnalyze.model.TransactionConnectionOutput;

public class TestRunner extends AbstractRunner {

	private LocalDate[] startDate = { 
//			LocalDate.of(2015, 10, 10),
			LocalDate.of(2015, 10, 10)	
	};
	
	private Sha256Hash[] startBlockHash = {
//			Sha256Hash.wrap("000000000000000004a89bd78bbaa336afb8ca11ef1e06af4f4617783a9dce12"),
			Sha256Hash.wrap("0000000001151b8e29b49b3821bb12ccfedf2f9dad77d3793e112852c1d6d260 ")
	};
	
	private Sha256Hash[] startTransactionHash = { null,
	};

	public static void main(String[] args) {
		int connectionsLimit = 100000;
		final TestRunner runner = new TestRunner();
		runner.run(connectionsLimit);
	}

	private void run(int connectionsLimit) {

		for (int i = 0; i < startBlockHash.length; i++) {
			FileLoader.setDir("D:/PWR/mgr/Praca Magisterska/BitCoinCore/BitcoinCoreInstall/blocks/");
			final BlocksConnectionFinder blocksConnectionFinder = new BlocksConnectionFinder();
			ArrayList<Sha256Hash> startTransactionHash = new ArrayList<>();
			TransactionConnectionInput transactionConnectionInput = new TransactionConnectionInput(startDate[i],
					startBlockHash[i], startTransactionHash, connectionsLimit);
			final IBitCoinIndexRepository repository = new HsqlBitcoinIndexRepository();
			final BlockChainByIndexReader blockchainReader = new BlockChainByIndexReader(repository );
			final TransactionConnectionOutput tc = blocksConnectionFinder.findConnections(transactionConnectionInput, blockchainReader, null);
//			writeToFile(tc.getConnections(), connectionsLimit, i, 0,  startTransactionHash.get(0));
//			tc.getConnections().stream().forEach(c -> System.out.println(c.toRCoinsString()));

		}

	}
}
