package bartoszzychal.BlockChainAnalyze.model;

import java.util.ArrayList;
import java.util.List;

public class TransactionConnectionOutput {

	private final TransactionConnectionInput transactionConnectionInput;
	private final List<ConnectionFinderOutput> connections;
	private final Boolean connectionsFoundSuccess;
	private final List<TransactionConnection> transactionConnections;
	
	public TransactionConnectionOutput(TransactionConnectionInput transactionConnectionInput,
			List<ConnectionFinderOutput> connections) {
		this.transactionConnectionInput = transactionConnectionInput;
		this.connections = connections;
		this.connectionsFoundSuccess = Boolean.TRUE;
		transactionConnections = null;
	}

	public TransactionConnectionOutput(TransactionConnectionInput transactionConnectionInput,
			ArrayList<TransactionConnection> connections) {
		this.transactionConnectionInput = null;
		this.connections = null;
		this.connectionsFoundSuccess = Boolean.TRUE;
		this.transactionConnections = connections;
	}

	public TransactionConnectionOutput(Boolean failed) {
		this.transactionConnectionInput = null;
		this.connections = null;
		this.connectionsFoundSuccess = failed;
		transactionConnections = null;
	}

	public TransactionConnectionInput getTransactionConnectionInput() {
		return transactionConnectionInput;
	}

	public List<ConnectionFinderOutput> getConnections() {
		return connections;
	}

	public Boolean getConnectionsFoundSuccess() {
		return connectionsFoundSuccess;
	}

	public List<TransactionConnection> getTransactionConnections() {
		return transactionConnections;
	}
	
}
