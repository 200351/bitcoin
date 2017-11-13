package bartoszzychal.BlockChainAnalyze;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDate;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.bitcoinj.core.Sha256Hash;
import org.junit.Test;

import bartoszzychal.BlockChainAnalyze.blockchainreader.IBlockChainReader;
import bartoszzychal.BlockChainAnalyze.blockchainreader.impl.BlockChainIndexDatabaseReader;
import bartoszzychal.BlockChainAnalyze.dbconnection.IBitCoinIndexRepository;
import bartoszzychal.BlockChainAnalyze.dbconnection.hsql.HsqlBitcoinIndexRepository;
import bartoszzychal.BlockChainAnalyze.fileloader.FileLoader;
import bartoszzychal.BlockChainAnalyze.finder.BlocksConnectionFinder;
import bartoszzychal.BlockChainAnalyze.model.TransactionConnection;
import bartoszzychal.BlockChainAnalyze.model.TransactionConnectionInput;
import bartoszzychal.BlockChainAnalyze.model.TransactionConnectionOutput;

public class BlocksConnectionFinderTest {

//	@Test
//	public void testFindConnections() {
//		final BlocksConnectionFinder blocksConnectionFinder = new BlocksConnectionFinder();
//		TransactionConnectionInput transactionConnectionInput = new TransactionConnectionInput(
//				LocalDate.of(2017, 07, 19),
//				Sha256Hash.wrap("000000000000000001341c2b83673eadf1db35c83d6e1fe11f0c888b091654a4"), 20);
//
//		final TransactionConnectionOutput findConnections = blocksConnectionFinder
//				.findConnections(transactionConnectionInput);
//
//	}
//
//	@Test
//	public void testFindConnections2() {
//		final BlocksConnectionFinder blocksConnectionFinder = new BlocksConnectionFinder();
//		TransactionConnectionInput transactionConnectionInput = new TransactionConnectionInput(LocalDate.now(),
//				Sha256Hash.wrap("0000000000000000005dd5f093020ee9e038fc654101133b7b3799055ca1d060"), 20);
//
//		final TransactionConnectionOutput findConnections = blocksConnectionFinder
//				.findConnections(transactionConnectionInput);
//
//	}

	@Test
	public void testFindConnections3() {
		FileLoader.setDir("D:/PWR/mgr/Praca Magisterska/BitCoinCore/BitcoinCoreInstall/blocks/");
		final BlocksConnectionFinder blocksConnectionFinder = new BlocksConnectionFinder();
		int connectionsLimit = 100000;
		TransactionConnectionInput transactionConnectionInput = new TransactionConnectionInput(
				LocalDate.of(2015, 10, 10),
				Sha256Hash.wrap("000000000000000004a89bd78bbaa336afb8ca11ef1e06af4f4617783a9dce12"),
				Sha256Hash.wrap("82e8621ba492cdd8101a78f7ecb886bb37cb5d577e4022db91cc9a6f81b9c5c6"), 
				connectionsLimit);

		final IBitCoinIndexRepository repository = new HsqlBitcoinIndexRepository();
		final IBlockChainReader blockchainReader = new BlockChainIndexDatabaseReader(repository);
		final TransactionConnectionOutput findConnections = blocksConnectionFinder
				.findConnections(transactionConnectionInput, blockchainReader);
		
		List<TransactionConnection> connections = findConnections.getConnections();
		System.out.println("Founded " + connections.size() + " Connections");
//		try {
//			File file = new File("D:\\PWR\\mgr\\Praca Magisterska\\R\\" + connectionsLimit  + ".csv");
//			OutputStream os = new FileOutputStream(file);
//			for (TransactionConnection transactionConnection : connections) {
//				IOUtils.write(transactionConnection.toRCoinsString() + "\n", os);
//			}
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
		connections.stream().forEach(c -> System.out.println(c.toRCoinsString()));
//		connections.stream().forEach(c -> IOUtils.write(c.toRString(), os));
		
	}
	
	@Test
	public void testFindConnections600() {
		FileLoader.setDir("D:/PWR/mgr/Praca Magisterska/BitCoinCore/BitcoinCoreInstall/blocks_t/");
		final BlocksConnectionFinder blocksConnectionFinder = new BlocksConnectionFinder();
		int connectionsLimit = 1000000;
		TransactionConnectionInput transactionConnectionInput = new TransactionConnectionInput(
				LocalDate.of(2016 , 8 ,17),
				Sha256Hash.wrap("0000000000000000020e3cd6ad7741043b93ddd9751c37d4f432fda2689938f3"),
				Sha256Hash.wrap("b18653335fce28d5034cd35a6e7ba67a4ad2033eae1e973606b3bfbbde539eff"), 
				connectionsLimit);
		
		final TransactionConnectionOutput findConnections = blocksConnectionFinder
				.findConnections(transactionConnectionInput, null);
		
		List<TransactionConnection> connections = findConnections.getConnections();
		System.out.println("Founded " + connections.size() + " Connections");
//		try {
//			File file = new File("D:\\PWR\\mgr\\Praca Magisterska\\R\\" + connectionsLimit  + ".csv");
//			OutputStream os = new FileOutputStream(file);
//			for (TransactionConnection transactionConnection : connections) {
//				IOUtils.write(transactionConnection.toRCoinsString() + "\n", os);
//			}
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
		connections.stream().forEach(c -> System.out.println(c.toRCoinsString()));
//		connections.stream().forEach(c -> IOUtils.write(c.toRString(), os));
		
	}
	@Test
	public void testFindConnections4() {
		final BlocksConnectionFinder blocksConnectionFinder = new BlocksConnectionFinder();
		int connectionsLimit = 100000;
		TransactionConnectionInput transactionConnectionInput = new TransactionConnectionInput(
				LocalDate.of(2015, 10, 10),
				Sha256Hash.wrap("000000000000000004a89bd78bbaa336afb8ca11ef1e06af4f4617783a9dce12"),
				Sha256Hash.wrap("82e8621ba492cdd8101a78f7ecb886bb37cb5d577e4022db91cc9a6f81b9c5c6"), 
				connectionsLimit);
		
		final TransactionConnectionOutput findConnections = blocksConnectionFinder
				.findConnections(transactionConnectionInput, null);
		
		List<TransactionConnection> connections = findConnections.getConnections();
		System.out.println("Founded " + connections.size() + " Connections");
		try {
			File file = new File("D:\\PWR\\mgr\\Praca Magisterska\\R\\" + connectionsLimit  + ".csv");
			OutputStream os = new FileOutputStream(file);
			for (TransactionConnection transactionConnection : connections) {
				IOUtils.write(transactionConnection.toRCoinsString() + "\n", os);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
//		connections.stream().forEach(c -> System.out.println(c.toRString()));
//		connections.stream().forEach(c -> IOUtils.write(c.toRString(), os));
		
		
	}
	@Test
	public void testFindConnections() {
		final BlocksConnectionFinder blocksConnectionFinder = new BlocksConnectionFinder();
		int connectionsLimit = 100000;
		TransactionConnectionInput transactionConnectionInput = new TransactionConnectionInput(
				LocalDate.of(2015, 10, 10),
				Sha256Hash.wrap("000000000000000004a89bd78bbaa336afb8ca11ef1e06af4f4617783a9dce12"),
				Sha256Hash.wrap("82e8621ba492cdd8101a78f7ecb886bb37cb5d577e4022db91cc9a6f81b9c5c6"), 
				connectionsLimit);
		
		final TransactionConnectionOutput findConnections = blocksConnectionFinder
				.findConnections(transactionConnectionInput, null);
		
		List<TransactionConnection> connections = findConnections.getConnections();
		System.out.println("Founded " + connections.size() + " Connections");
//		try {
//			File file = new File("D:\\PWR\\mgr\\Praca Magisterska\\R\\" + connectionsLimit  + ".csv");
//			OutputStream os = new FileOutputStream(file);
//			for (TransactionConnection transactionConnection : connections) {
//				IOUtils.write(transactionConnection.toRString() + "\n", os);
//			}
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
		connections.stream().forEach(c -> System.out.println(c.toRCoinsString()));
//		connections.stream().forEach(c -> IOUtils.write(c.toRString(), os));
		
		
	}

}
