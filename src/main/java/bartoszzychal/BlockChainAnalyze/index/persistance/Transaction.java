package bartoszzychal.BlockChainAnalyze.index.persistance;

import java.util.Set;

import javax.persistence.AttributeOverride;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.ManyToAny;

@Entity
@Table(name = "Transaction", schema="blockIndex")
@AttributeOverride(name = "id", column = @Column(name = "transactionId"))
public class Transaction extends FlatEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Column(name = "transactionHash")
	private String transactionHash;
	
	@Column(name = "inputSum")
	private long inputSum;
	
	@Column(name = "outputSum")
	private long outputSum;
	
	@OneToMany(mappedBy = "transaction", fetch=FetchType.LAZY)
	private Set<TransactionInput> inputs;
	
	@OneToMany(mappedBy = "transaction", fetch=FetchType.LAZY)
	private Set<TransactionOutput> outputs;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "blockId", nullable = false)
	private BlockIndex blockIndex;
	
	public String getTransactionHash() {
		return transactionHash;
	}

	public void setTransactionHash(String transactionHash) {
		this.transactionHash = transactionHash;
	}

	public long getInputSum() {
		return inputSum;
	}

	public void setInputSum(long inputSum) {
		this.inputSum = inputSum;
	}

	public long getOutputSum() {
		return outputSum;
	}
	public void setOutputSum(long outputSum) {
		this.outputSum = outputSum;
	}

	public Set<TransactionInput> getInputs() {
		return inputs;
	}

	public void setInputs(Set<TransactionInput> inputs) {
		this.inputs = inputs;
	}

	public Set<TransactionOutput> getOutputs() {
		return outputs;
	}

	public void setOutputs(Set<TransactionOutput> outputs) {
		this.outputs = outputs;
	}

	public BlockIndex getBlockIndex() {
		return blockIndex;
	}

	public void setBlockIndex(BlockIndex blockIndex) {
		this.blockIndex = blockIndex;
	}
	 
}
