package bartoszzychal.BlockChainAnalyze.index.persistance;

import java.time.LocalDateTime;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "BlockIndex", schema="blockIndex")
public class BlockIndex extends FlatEntity  {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Column(name = "blockHash", unique = true)
	private String blockHash;

	@Column(name = "prevBlockHash")
	private String prevBlockHash;

	@Column(name = "fileName")
	private String fileName;

	@Column(name = "startFromByte")
	private Long startFromByte;

	@Column(name = "generatedDate")
	private LocalDateTime generatedDate;

	@OneToMany(mappedBy= "blockIndex", fetch=FetchType.LAZY)
	private Set<Transaction> transactions;

	public BlockIndex() {
	}

	public String getBlockHash() {
		return blockHash;
	}

	public void setBlockHash(String bitcoinHash) {
		this.blockHash = bitcoinHash;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public Long getStartFromByte() {
		return startFromByte;
	}

	public void setStartFromByte(Long startFromByte) {
		this.startFromByte = startFromByte;
	}

	public LocalDateTime getGeneratedDate() {
		return generatedDate;
	}

	public void setGeneratedDate(LocalDateTime generatedDate) {
		this.generatedDate = generatedDate;
	}
	
	public Set<Transaction> getTransactions() {
		return transactions;
	}

	public void setTransactions(Set<Transaction> transactions) {
		this.transactions = transactions;
	}

	public String getPrevBlockHash() {
		return prevBlockHash;
	}

	public void setPrevBlockHash(String prevBlockHash) {
		this.prevBlockHash = prevBlockHash;
	}

	@Override
	public String toString() {
		return "BlockIndex [blockHash=" + blockHash + ", prevBlockHash=" + prevBlockHash + ", fileName=" + fileName
				+ ", startFromByte=" + startFromByte + ", generatedDate=" + generatedDate + "]";
	}
	
	
}
