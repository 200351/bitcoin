package bartoszzychal.BlockChainAnalyze.persistance;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "BlockIndex")
public class BlockIndex extends BaseEntity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Column(name = "blockHash", unique= true)
	private String blockHash;

	@Column(name = "fileName")
	private String fileName;
	
	@Column(name = "startFromByte")
	private Long startFromByte;
	
	@Column(name = "generatedDate")
	private LocalDateTime generatedDate;

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

	@Override
	public String toString() {
		return "BlockIndex [bitcoinHash=" + blockHash + ", fileName=" + fileName + ", startFromByte=" + startFromByte
				+ ", generatedDate=" + generatedDate + "]";
	}
}
