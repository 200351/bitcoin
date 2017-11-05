package bartoszzychal.BlockChainAnalyze.model;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections4.CollectionUtils;
import org.bitcoinj.core.Sha256Hash;

public class TransactionConnection {
	private Sha256Hash inputBlockHash;
	private Sha256Hash outputBlockHash;
	private Sha256Hash inputTransactionHash;
	private Sha256Hash outputTransactionHash;
	private Set<String> inputAddresses;
	private Set<String> outputAddresses;
	private Set<String> connectedAddresses;
	private long inputTime;
	private long outputTime;

	public TransactionConnection(TransactionSearchInfo tc) {
		this.setInputAddresses(tc.getAddresses());
		this.setInputBlockHash(tc.getBlockHash());
		this.setInputTransactionHash(tc.getTransactionHash());
		this.setInputTime(tc.getTime());
	}

	public TransactionConnection() {
	}

	public boolean isPreviousConnected(final Set<Sha256Hash> outputHashes) {
		if (CollectionUtils.isNotEmpty(outputHashes)) {
			return new HashSet<>(inputAddresses).retainAll(outputHashes);
		}
		return false;
	}

	public boolean isNextConnected(final List<Sha256Hash> inputHashes) {
		if (CollectionUtils.isNotEmpty(inputHashes)) {
			return new HashSet<>(outputAddresses).retainAll(inputHashes);
		}
		return false;
	}

	public boolean isConnected() {
		return connectedAddresses != null && connectedAddresses.size() > 0;
	}

	public Sha256Hash getInputBlockHash() {
		return inputBlockHash;
	}

	public void setInputBlockHash(Sha256Hash inputBlockHash) {
		this.inputBlockHash = inputBlockHash;
	}

	public Sha256Hash getOutputBlockHash() {
		return outputBlockHash;
	}

	public void setOutputBlockHash(Sha256Hash outputBlockHash) {
		this.outputBlockHash = outputBlockHash;
	}

	public Sha256Hash getInputTransactionHash() {
		return inputTransactionHash;
	}

	public void setInputTransactionHash(Sha256Hash inputTransactionHash) {
		this.inputTransactionHash = inputTransactionHash;
	}

	public Sha256Hash getOutputTransactionHash() {
		return outputTransactionHash;
	}

	public void setOutputTransactionHash(Sha256Hash outputTransactionHash) {
		this.outputTransactionHash = outputTransactionHash;
	}

	public Set<String> getInputAddresses() {
		return inputAddresses;
	}

	public void setInputAddresses(Set<String> inputAddresses) {
		this.inputAddresses = inputAddresses;
	}

	public Set<String> getOutputAddresses() {
		return outputAddresses;
	}

	public void setOutputAddresses(Set<String> outputAddresses) {
		this.outputAddresses = outputAddresses;
	}

	public Set<String> getConnectedAddresses() {
		return connectedAddresses;
	}

	public void setConnectedAddresses(Set<String> connectedAddresses) {
		this.connectedAddresses = connectedAddresses;
	}

	public long getInputTime() {
		return inputTime;
	}

	public void setInputTime(long inputTime) {
		this.inputTime = inputTime;
	}

	public long getOutputTime() {
		return outputTime;
	}

	public void setOutputTime(long outputTime) {
		this.outputTime = outputTime;
	}

	@Override
	public String toString() {
		return "TransactionConnection [inputBlockHash=" + inputBlockHash + ", outputBlockHash=" + outputBlockHash
				+ ", inputTransactionHash=" + inputTransactionHash + ", outputTransactionHash=" + outputTransactionHash
				+ ", inputAddresses=" + inputAddresses + ", outputAddresses=" + outputAddresses
				+ ", connectedAddresses=" + connectedAddresses + ", inputTime=" + inputTime + ", outputTime="
				+ outputTime + "]";
	}

	
}
