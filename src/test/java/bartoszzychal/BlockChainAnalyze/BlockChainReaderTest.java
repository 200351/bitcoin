package bartoszzychal.BlockChainAnalyze;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.List;

import org.bitcoinj.core.Block;
import org.bitcoinj.core.PrunedException;
import org.bitcoinj.core.Transaction;
import org.bitcoinj.core.TransactionInput;
import org.bitcoinj.core.TransactionOutput;
import org.bitcoinj.core.VerificationException;
import org.bitcoinj.store.BlockStoreException;
import org.junit.Test;

import bartoszzychal.BlockChainAnalyze.blockchainreader.IBlockChainReader;
import bartoszzychal.BlockChainAnalyze.blockchainreader.impl.BlockChainReverseReader;

public class BlockChainReaderTest {

	
	@Test
	public void testReadBlockChainFromTo() throws VerificationException, BlockStoreException, PrunedException {
		// first block 2009-01-03
		LocalDate to = LocalDate.of(2017, 07, 19);//2017-07-19
		LocalDate from = to.minusDays(3);
		
		IBlockChainReader blockChainReader = BlockChainReverseReader.getInstance();
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		
		while (true) {
//		while (to.isBefore(LocalDate.of(2010, 01, 01))) {
			List<Block> blocks = blockChainReader.readBlockChainFromTo(from, to);
//			blocks.stream().forEach(b -> System.out.println(format.format(b.getTime())));
			from = from.minusDays(10);
			to = to.minusDays(10);
			for (Block block : blocks) {
				System.out.println("Block Hash " + block.getHashAsString());
				List<Transaction> transactions = block.getTransactions();
				for (Transaction transaction : transactions) {
					System.out.println("InputSum " + transaction.getInputSum());
					System.out.println("OutputSum " + transaction.getOutputSum());
					System.out.println("Fee " + transaction.getFee());
					System.out.println("Transaction Hash " + transaction.getHash());
					List<TransactionInput> inputs = transaction.getInputs();
					List<TransactionOutput> outputs = transaction.getOutputs();
					for (TransactionInput transactionInput : inputs) {
						if (transactionInput != null) {
							System.out.println("InputCoins " + transactionInput.getValue());
							TransactionOutput connectedOutput = transactionInput.getConnectedOutput();
							if (connectedOutput != null) {
								System.out.println("Connected Transaction " + connectedOutput.getValue());
								if (outputs.contains(connectedOutput)) {
									System.out.println("The same transaction");
									outputs.get(outputs.indexOf(connectedOutput));
								}
								System.out.println("The another transaction");
							}
						}
					}
					for (TransactionOutput transactionOutput : outputs) {
						if (transactionOutput != null) {
							System.out.println("OutputCoins " + transactionOutput.getValue());
						}
					}
				}
			}
		}

	}

}
