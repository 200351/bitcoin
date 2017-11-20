package bartoszzychal.BlockChainAnalyze.mapper;

import org.bitcoinj.core.Block;
import org.bitcoinj.core.Transaction;

import bartoszzychal.BlockChainAnalyze.generator.TransactionSearchInfoGenerator;
import bartoszzychal.BlockChainAnalyze.model.ConnectionFinderInput;

public class ConnectionFinderInputMapper {

	public static ConnectionFinderInput map(Transaction transaction, Block block, long limit) {
		final ConnectionFinderInput connectionFinderInput = new ConnectionFinderInput(limit);
		connectionFinderInput.addConnectionToFind(TransactionSearchInfoGenerator.generate(transaction, block));
		connectionFinderInput.setStartBlock(block);
		connectionFinderInput.setStartTransaction(transaction);
		return connectionFinderInput;
	}
}
