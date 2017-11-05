package bartoszzychal.BlockChainAnalyze.model;

import java.util.List;

import org.bitcoinj.core.Sha256Hash;

public class TransactionConnectionOutput {

	private final TransactionConnectionInput transactionConnectionInput;
	private final List<TransactionConnection> connections;

	public TransactionConnectionOutput(TransactionConnectionInput transactionConnectionInput,
			List<TransactionConnection> connections) {
		this.transactionConnectionInput = transactionConnectionInput;
		this.connections = connections;
	}

	public TransactionConnectionInput getTransactionConnectionInput() {
		return transactionConnectionInput;
	}

	public List<TransactionConnection> getConnections() {
		return connections;
	}

}
