package bartoszzychal.BlockChainAnalyze.finder;

import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;

import bartoszzychal.BlockChainAnalyze.generator.TransactionSearchInfoGenerator;
import bartoszzychal.BlockChainAnalyze.model.ConnectionFinderInput;
import bartoszzychal.BlockChainAnalyze.model.TransactionConnection;
import bartoszzychal.BlockChainAnalyze.model.TransactionSearchInfo;

public class ConnectionFinder implements Runnable {

    private static final Logger log = Logger.getLogger(ConnectionFinder.class);

	private ConnectionFinderInput input;

	public ConnectionFinder(final ConnectionFinderInput input) {
		this.input = input;
	}

	@Override
	public void run() {
		final Set<TransactionSearchInfo> newConnectionsToFind = new HashSet<>();
//		log.info("Start search in Transaction " + input.getTransaction().getHashAsString());
		for (TransactionSearchInfo connectionInfo : input.getConnectionsToFind()) {

			// Check if at least one input transaction is connected
			// to output the transaction from this block.
			TransactionConnection tc = TransactionChecker.areTransactionsConnected(connectionInfo,
					input.getOutputsInfo(), input.getTransaction(), input.getBlock());
			// If is connected:
			if (tc != null && tc.isConnected()) {
				// add Connection to found connection
				input.addFoundConnection(tc);
				// check if limit is obtained
				if (input.isLimitObtained()) break;
				// create new Connection to found from this
				// Transaction Inputs.
				final TransactionSearchInfo newConnectionToFind = TransactionSearchInfoGenerator
						.generate(input.getTransaction(), input.getBlock());
				// add to List new connections to find.
				newConnectionsToFind.add(newConnectionToFind);
			}
		}
//		log.info("In Transaction " + input.getTransaction().getHashAsString());
//		log.info("Count of all found connections: " + input.getFoundConnection().size());
		// remove found connection
//		log.info("Count of new connections to find: " + newConnectionsToFind.size());
		// all 
//		log.info("Count of all connections to find: " + input.getConnectionsToFind().size());
		input.getConnectionsToFind().addAll(newConnectionsToFind);
		input.clearOutputsInfo();
	}

}
