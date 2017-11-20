package bartoszzychal.BlockChainAnalyze.dbconnection;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.bitcoinj.core.Block;

import bartoszzychal.BlockChainAnalyze.index.persistance.BlockIndex;
import bartoszzychal.BlockChainAnalyze.index.persistance.Transaction;

public interface IBitCoinIndexRepository extends IRepository {
	
	public BlockIndex readIndex(String blockHash);
	public List<BlockIndex> readIndexedBlocks(LocalDate start, LocalDate end);
	public List<BlockIndex> readIndexedBlocks(LocalDate start, LocalDate end, List<String> addresses);
	public BlockIndex createNewIndexForBlock(Block block, String fileName, Long startFromByte);
	public BlockIndex createNewIndexForBlock(BlockIndex blockIndex, boolean reindex);
	public void createNewIndexForBlock(List<BlockIndex> blockIndex, boolean reindex);
	void removeIndex(BlockIndex oldReadIndex);
	void createIndex(BlockIndex oldReadIndex);
	List<Transaction> readTransactionBlocks(LocalDateTime start, LocalDateTime end, List<String> addresses);
}
