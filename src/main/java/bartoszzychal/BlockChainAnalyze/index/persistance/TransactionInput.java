package bartoszzychal.BlockChainAnalyze.index.persistance;

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "TransactionInput", schema="blockIndex")
@AttributeOverride(name = "id", column = @Column(name = "inputId"))
public class TransactionInput extends FlatEntity {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Column(name = "address")
	private String address;
	
	@Column(name = "coins")
	private long coins;
	
	public TransactionInput() {
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public long getCoins() {
		return coins;
	}

	public void setCoins(long coins) {
		this.coins = coins;
	}
	
}
