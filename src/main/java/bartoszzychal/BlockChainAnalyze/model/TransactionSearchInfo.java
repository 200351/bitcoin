package bartoszzychal.BlockChainAnalyze.model;

import java.util.Set;

import org.bitcoinj.core.Sha256Hash;

public class TransactionSearchInfo {
	private Sha256Hash blockHash;
	private Sha256Hash transactionHash;
	private Set<AbstractInfo> info;
	private long time;
	
	public Sha256Hash getBlockHash() {
		return blockHash;
	}
	public void setBlockHash(Sha256Hash blockHash) {
		this.blockHash = blockHash;
	}
	public Sha256Hash getTransactionHash() {
		return transactionHash;
	}
	public void setTransactionHash(Sha256Hash transactionHash) {
		this.transactionHash = transactionHash;
	}
	
	public Set<AbstractInfo> getInfo() {
		return info;
	}
	public void setInfo(Set<AbstractInfo> info) {
		this.info = info;
	}
	public long getTime() {
		return time;
	}
	public void setTime(long time) {
		this.time = time;
	}
}
