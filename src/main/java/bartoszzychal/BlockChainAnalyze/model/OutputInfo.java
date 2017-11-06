package bartoszzychal.BlockChainAnalyze.model;

import org.bitcoinj.core.Coin;

public class OutputInfo extends AbstractInfo {

	public OutputInfo(Coin coins, String address) {
		super(coins, address);
	}

	public ConnectedInfo toConnectedInfo() {
		return new ConnectedInfo(getCoins(), getAddress());
	}
	
}
