package bartoszzychal.BlockChainAnalyze.model;

import org.bitcoinj.core.Coin;

public class InputInfo extends AbstractInfo {

	public InputInfo(Coin coins, String address) {
		super(coins, address);
	}
	
}
