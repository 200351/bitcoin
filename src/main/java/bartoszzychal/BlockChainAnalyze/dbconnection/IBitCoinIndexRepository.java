package bartoszzychal.BlockChainAnalyze.dbconnection;

import java.time.LocalDate;
import java.util.List;

import org.bitcoinj.core.Block;

import bartoszzychal.BlockChainAnalyze.index.persistance.BlockIndex;

public interface IBitCoinIndexRepository extends IRepository {
	public BlockIndex readIndex(String blockHash);
	public List<BlockIndex> readIndexedBlocks(LocalDate start, LocalDate end);
	public List<BlockIndex> readIndexedBlocks(LocalDate start, LocalDate end, List<String> addresses);
	public BlockIndex createNewIndexForBlock(Block block, String fileName, Long startFromByte);
	public BlockIndex createNewIndexForBlock(BlockIndex blockIndex, boolean reindex);
	void removeIndex(BlockIndex oldReadIndex);
	void createIndex(BlockIndex oldReadIndex);
}
