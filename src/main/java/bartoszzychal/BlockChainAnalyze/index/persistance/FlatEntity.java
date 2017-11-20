package bartoszzychal.BlockChainAnalyze.index.persistance;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.SequenceGenerator;

@MappedSuperclass
public abstract class FlatEntity implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	@Id
	@Column(name = "id", unique = true, nullable = false)
	@SequenceGenerator(name = "SequenceIdGenerator", sequenceName = "SEQ_BLOCKINDEX", allocationSize=1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SequenceIdGenerator")
	private Long id;
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
	}
	
}
