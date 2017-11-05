package bartoszzychal.BlockChainAnalyze.model;

import java.time.LocalDate;

import org.bitcoinj.core.Sha256Hash;

public class TransactionConnectionInput {

	private final LocalDate startDate;
	private final Sha256Hash startBlockHash;
	private final Sha256Hash startTransactionHash;
	private final long connectionsLimit;

	public TransactionConnectionInput(LocalDate startDate, Sha256Hash startBlockHash, Sha256Hash startTransactionHash,
			long connectionsLimit) {
		this.startDate = startDate;
		this.startBlockHash = startBlockHash;
		this.startTransactionHash = startTransactionHash;
		this.connectionsLimit = connectionsLimit;
	}

	public TransactionConnectionInput(LocalDate startDate, Sha256Hash startBlockHash, long connectionsLimit) {
		this.startDate = startDate;
		this.startBlockHash = startBlockHash;
		this.startTransactionHash = null;
		this.connectionsLimit = connectionsLimit;
	}

	public TransactionConnectionInput(Sha256Hash startBlockHash, Sha256Hash startTransactionHash,
			long connectionsLimit) {
		this.startBlockHash = startBlockHash;
		this.startTransactionHash = startTransactionHash;
		this.connectionsLimit = connectionsLimit;
		this.startDate = null;
	}

	public TransactionConnectionInput(Sha256Hash startBlockHash, long connectionsLimit) {
		this.startBlockHash = startBlockHash;
		this.startTransactionHash = null;
		this.startDate = null;
		this.connectionsLimit = connectionsLimit;
	}

	public long getConnectionsLimit() {
		return connectionsLimit;
	}

	public Sha256Hash getStartBlockHash() {
		return startBlockHash;
	}

	public Sha256Hash getStartTransactionHash() {
		return startTransactionHash;
	}

	public LocalDate getStartDate() {
		return startDate;
	}

}
