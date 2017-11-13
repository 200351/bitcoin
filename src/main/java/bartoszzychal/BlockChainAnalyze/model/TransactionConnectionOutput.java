package bartoszzychal.BlockChainAnalyze.model;

import java.util.List;

public class TransactionConnectionOutput {

	private final TransactionConnectionInput transactionConnectionInput;
	private final List<TransactionConnection> connections;
	private final Boolean connectionsFoundSuccess;
	
	public TransactionConnectionOutput(TransactionConnectionInput transactionConnectionInput,
			List<TransactionConnection> connections) {
		this.transactionConnectionInput = transactionConnectionInput;
		this.connections = connections;
		this.connectionsFoundSuccess = Boolean.TRUE;
	}

	public TransactionConnectionOutput(Boolean failed) {
		this.transactionConnectionInput = null;
		this.connections = null;
		this.connectionsFoundSuccess = failed;
	}

	public TransactionConnectionInput getTransactionConnectionInput() {
		return transactionConnectionInput;
	}

	public List<TransactionConnection> getConnections() {
		return connections;
	}

	public Boolean getConnectionsFoundSuccess() {
		return connectionsFoundSuccess;
	}

}
