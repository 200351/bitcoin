package bartoszzychal.BlockChainAnalyze.model;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.commons.io.IOUtils;
import org.bitcoinj.core.Block;
import org.bitcoinj.core.Transaction;

public class ConnectionFinderInput {
	
	private final AtomicLong idSequence;
	private final Set<TransactionSearchInfo> connectionsToFind;
	private final Set<TransactionConnection> foundConnection;
	private Set<OutputInfo> outputsInfo;
	private Transaction transaction;
	private Block block;
	private Transaction startTransaction;
	private Block startBlock;
	private final long limit;
	private FileOutputStream coinsFileOutput;
	private FileOutputStream timeFileOutput;
	private FileOutputStream fullFileOutput;

	public ConnectionFinderInput(long limit) {
		this.limit = limit;
		this.idSequence = new AtomicLong(1);
		this.connectionsToFind = new HashSet<>();
		this.foundConnection = new HashSet<>();
		this.outputsInfo = Collections.synchronizedSet(new HashSet<>());
	}
	
	public void addConnectionToFind(TransactionSearchInfo tsi) {
		connectionsToFind.add(tsi);
	}

	public void addFoundConnection(TransactionConnection tsi) {
		tsi.setId(Long.valueOf(idSequence.getAndIncrement()));
		foundConnection.add(tsi);
		try {
			IOUtils.write(tsi.toRCoinsString() + "\n", getCoinsFileOutput());
			IOUtils.write(tsi.toRTimeString() + "\n", getTimeFileOutput());
			IOUtils.write(tsi.toFullString() + "\n", getFullFileOutput());
			getCoinsFileOutput().flush();
			getTimeFileOutput().flush();
			getFullFileOutput().flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public Set<TransactionSearchInfo> getConnectionsToFind() {
		return connectionsToFind;
	}

	public Set<TransactionConnection> getFoundConnection() {
		return foundConnection;
	}

	public Set<OutputInfo> getOutputsInfo() {
		return outputsInfo;
	}

	public void setOutputsInfo(Set<OutputInfo> outputsInfo) {
		this.outputsInfo = outputsInfo;
	}
	
	public void clearOutputsInfo() {
		this.outputsInfo.clear();
	}

	public Transaction getTransaction() {
		return transaction;
	}

	public void setTransaction(Transaction transaction) {
		this.transaction = transaction;
	}

	public Block getBlock() {
		return block;
	}

	public void setBlock(Block block) {
		this.block = block;
	}

	public Block getStartBlock() {
		return startBlock;
	}

	public void setStartBlock(Block startBlock) {
		this.startBlock = startBlock;
	}

	public Transaction getStartTransaction() {
		return startTransaction;
	}

	public void setStartTransaction(Transaction startTransaction) {
		this.startTransaction = startTransaction;
	}

	public boolean isLimitObtained() {
		return idSequence.get() > limit;
	}

	public FileOutputStream getCoinsFileOutput() {
		return coinsFileOutput;
	}

	public void setCoinsFileOutput(File coinsFileOutput) {
		try {
			this.coinsFileOutput =  new FileOutputStream(coinsFileOutput);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public FileOutputStream getTimeFileOutput() {
		return timeFileOutput;
	}

	public void setTimeFileOutput(File timeFileOutput) {
		try {
			this.timeFileOutput = new FileOutputStream(timeFileOutput);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public FileOutputStream getFullFileOutput() {
		return fullFileOutput;
	}

	public void setFullFileOutput(File fullFileOutput) {
		try {
			this.fullFileOutput = new FileOutputStream(fullFileOutput);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public void closeFiles() {
		try {
			coinsFileOutput.close();
			fullFileOutput.close();
			timeFileOutput.close();
		} catch (IOException e) {
			e.printStackTrace();
		}		
	}
}
