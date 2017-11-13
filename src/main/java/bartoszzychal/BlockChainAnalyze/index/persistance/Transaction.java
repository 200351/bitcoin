package bartoszzychal.BlockChainAnalyze.index.persistance;

import java.util.Set;

import javax.persistence.AttributeOverride;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "Transaction")
@AttributeOverride(name = "id", column = @Column(name = "transationId"))
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
	
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@JoinColumn(name = "transationId")
	 private Set<TransactionInput> inputs;
	
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@JoinColumn(name = "transationId")
	private Set<TransactionOutput> outputs;

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
	 
}
