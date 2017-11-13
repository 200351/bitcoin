package bartoszzychal.BlockChainAnalyze.model;

import java.util.Set;

import org.apache.commons.collections4.CollectionUtils;
import org.bitcoinj.core.Coin;
import org.bitcoinj.core.Sha256Hash;

import bartoszzychal.BlockChainAnalyze.mapper.InfoMapper;

public class TransactionConnection {

	private Sha256Hash inputBlockHash;
	private Sha256Hash outputBlockHash;
	private Sha256Hash inputTransactionHash;
	private Sha256Hash outputTransactionHash;

	private long inputTime;
	private long outputTime;

	private Set<InputInfo> inputInfo;
	private Set<OutputInfo> outputInfo;
	private Set<ConnectedInfo> connectedInfo;

	public TransactionConnection(TransactionSearchInfo tc) {
		this.setInputInfo(InfoMapper.map(tc));
		this.setInputBlockHash(tc.getBlockHash());
		this.setInputTransactionHash(tc.getTransactionHash());
		this.setInputTime(tc.getTime());
	}

	public TransactionConnection() {
	}

	public boolean isConnected() {
		return connectedInfo != null && connectedInfo.size() > 0;
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
	

	public Set<InputInfo> getInputInfo() {
		return inputInfo;
	}

	public void setInputInfo(Set<InputInfo> inputInfo) {
		this.inputInfo = inputInfo;
	}

	public Set<OutputInfo> getOutputInfo() {
		return outputInfo;
	}

	public void setOutputInfo(Set<OutputInfo> outputInfo) {
		this.outputInfo = outputInfo;
	}

	public Set<ConnectedInfo> getConnectedInfo() {
		return connectedInfo;
	}

	public void setConnectedInfo(Set<ConnectedInfo> connectedInfo) {
		this.connectedInfo = connectedInfo;
	}

	@Override
	public String toString() {
		return "TransactionConnection [inputBlockHash=" + inputBlockHash + ", outputBlockHash=" + outputBlockHash
				+ ", inputTransactionHash=" + inputTransactionHash + ", outputTransactionHash=" + outputTransactionHash
				+ ", inputAddresses=" + inputInfo + ", outputAddresses=" + outputInfo + ", connectedAddresses="
				+ reduceAddresses(connectedInfo) + ", connectedCoin=" + countCoins(connectedInfo).toFriendlyString() + ", inputCoins="
				+ countCoins(inputInfo).toFriendlyString() + ", outputCoin=" + countCoins(outputInfo).toFriendlyString()
				+ ", inputTime=" + inputTime + ", outputTime=" + outputTime + "]";
	}

	private Coin countCoins(Set<? extends AbstractInfo> info) {
		Coin coins = Coin.ZERO;
		if (CollectionUtils.isNotEmpty(info)) {
			coins = info.stream().map(i -> i.getCoins() != null ? i.getCoins() : Coin.ZERO).reduce((a, b) -> a.plus(b)).orElse(Coin.ZERO);
		}
		return coins;
	}

	private String reduceAddresses(Set<? extends AbstractInfo> info) {
		String addresses = "";
		if (CollectionUtils.isNotEmpty(info)) {
			addresses = info.stream().map(c -> c.getAddress()).reduce((a1, a2) -> a1 + ", " + a2).get();
		}
		return addresses;
	}

	public String toShortString() {
		return "InputTransactionHash=" + inputTransactionHash + ", outputTransactionHash=" + outputTransactionHash
				+ ", outputCoin=" + countCoins(outputInfo).toFriendlyString() + ", connectedAddresses=" + reduceAddresses(connectedInfo)
				+ ", connectedCoin=" + countCoins(connectedInfo).toFriendlyString();
	}

	public String toFullString() {
		return inputTransactionHash + " " + outputTransactionHash + " " + inputBlockHash + " " + outputBlockHash + " "
				+ outputTime + " " + countCoins(connectedInfo).toFriendlyString();
	}

	public String toRCoinsString() {
		return inputTransactionHash + " " + outputTransactionHash + " " + countCoins(connectedInfo).toPlainString();
	}
	
	public String toRTimeString() {
		return inputTransactionHash + " " + outputTransactionHash + " " + outputTime;
	}

}
