package bartoszzychal.BlockChainAnalyze;

import java.time.LocalDate;
import java.util.List;

import org.apache.commons.collections4.bag.SynchronizedSortedBag;
import org.bitcoinj.core.Sha256Hash;
import org.junit.Test;

import bartoszzychal.BlockChainAnalyze.model.TransactionConnection;
import bartoszzychal.BlockChainAnalyze.model.TransactionConnectionInput;
import bartoszzychal.BlockChainAnalyze.model.TransactionConnectionOutput;

public class BlocksConnectionFinderTest {

	@Test
	public void testFindConnections() {
		final BlocksConnectionFinder blocksConnectionFinder = new BlocksConnectionFinder();
		TransactionConnectionInput transactionConnectionInput = new TransactionConnectionInput(
				LocalDate.of(2017, 07, 19),
				Sha256Hash.wrap("000000000000000001341c2b83673eadf1db35c83d6e1fe11f0c888b091654a4"), 20);

		final TransactionConnectionOutput findConnections = blocksConnectionFinder
				.findConnections(transactionConnectionInput);

	}

	@Test
	public void testFindConnections2() {
		final BlocksConnectionFinder blocksConnectionFinder = new BlocksConnectionFinder();
		TransactionConnectionInput transactionConnectionInput = new TransactionConnectionInput(LocalDate.now(),
				Sha256Hash.wrap("0000000000000000005dd5f093020ee9e038fc654101133b7b3799055ca1d060"), 20);

		final TransactionConnectionOutput findConnections = blocksConnectionFinder
				.findConnections(transactionConnectionInput);

	}

	@Test
	public void testFindConnections3() {
		final BlocksConnectionFinder blocksConnectionFinder = new BlocksConnectionFinder();
		TransactionConnectionInput transactionConnectionInput = new TransactionConnectionInput(
				LocalDate.of(2015, 10, 10),
				Sha256Hash.wrap("000000000000000004a89bd78bbaa336afb8ca11ef1e06af4f4617783a9dce12"),
				Sha256Hash.wrap("82e8621ba492cdd8101a78f7ecb886bb37cb5d577e4022db91cc9a6f81b9c5c6"), 
				10000);

		final TransactionConnectionOutput findConnections = blocksConnectionFinder
				.findConnections(transactionConnectionInput);
		
		List<TransactionConnection> connections = findConnections.getConnections();
		System.out.println("Founded " + connections.size() + " Connections");
		connections.stream().forEach(System.out::println);
		
	}

}
