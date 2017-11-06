package bartoszzychal.BlockChainAnalyze.model;

import org.bitcoinj.core.Coin;

public abstract class AbstractInfo {
	private final Coin coins;
	private final String address;

	public AbstractInfo(Coin coins, String address) {
		this.coins = coins;
		this.address = address;
	}
	
	public Coin getCoins() {
		return coins;
	}

	public String getAddress() {
		return address;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((address == null) ? 0 : address.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass().isAssignableFrom(obj.getClass()))
			return false;
		AbstractInfo other = (AbstractInfo) obj;
		if (address == null) {
			if (other.address != null)
				return false;
		} else if (!address.equals(other.address))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return address;
	}
	
}
