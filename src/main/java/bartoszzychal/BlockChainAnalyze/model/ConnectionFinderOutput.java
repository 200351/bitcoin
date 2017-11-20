package bartoszzychal.BlockChainAnalyze.model;

import java.util.HashSet;
import java.util.Set;

import org.bitcoinj.core.Block;
import org.bitcoinj.core.Transaction;

public class ConnectionFinderOutput {
	
	private Set<TransactionConnection> foundConnection;
	private Transaction startTransaction;
	private Block startBlock;
	private boolean success;
	
	public ConnectionFinderOutput() {
		this.foundConnection = new HashSet<>();
	}

	public Transaction getStartTransaction() {
		return startTransaction;
	}

	public void setStartTransaction(Transaction startTransaction) {
		this.startTransaction = startTransaction;
	}

	public Block getStartBlock() {
		return startBlock;
	}

	public void setStartBlock(Block startBlock) {
		this.startBlock = startBlock;
	}

	public Set<TransactionConnection> getFoundConnection() {
		return foundConnection;
	}

	public void setFoundConnection(Set<TransactionConnection> foundConnection) {
		this.foundConnection = foundConnection;
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}
	
}
