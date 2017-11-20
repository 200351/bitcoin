package bartoszzychal.BlockChainAnalyze.model;

import java.time.LocalDate;
import java.util.List;

import org.bitcoinj.core.Sha256Hash;

public class TransactionConnectionInput {

	private final LocalDate startDate;
	private final Sha256Hash startBlockHash;
	private final List<Sha256Hash> startTransactionHash;
	private final long connectionsLimit;
	private String outputDirectory;

	public TransactionConnectionInput(LocalDate startDate, Sha256Hash startBlockHash, List<Sha256Hash> startTransactionHash,
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

	public TransactionConnectionInput(Sha256Hash startBlockHash, List<Sha256Hash> startTransactionHash,
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

	public synchronized Sha256Hash getStartBlockHash() {
		return startBlockHash;
	}

	public synchronized List<Sha256Hash> getStartTransactionHash() {
		return startTransactionHash;
	}

	public LocalDate getStartDate() {
		return startDate;
	}

	@Override
	public String toString() {
		return "TransactionConnectionInput [startDate=" + startDate + ", startBlockHash=" + startBlockHash
				+ ", startTransactionHash=" + startTransactionHash + ", connectionsLimit=" + connectionsLimit + "]";
	}

	public String getOutputDirectory() {
		return outputDirectory;
	}

	public void setOutputDirectory(String outputDirectory) {
		this.outputDirectory = outputDirectory;
	}

}
