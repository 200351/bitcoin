package bartoszzychal.BlockChainAnalyze.dbconnection;

import java.time.LocalDate;
import java.util.List;

import org.bitcoinj.core.Block;

import bartoszzychal.BlockChainAnalyze.persistance.BlockIndex;

public interface IBitCoinIndexRepository extends IRepository {
	public BlockIndex readIndex(String blockHash);
	public List<BlockIndex> readIndexedBlocks(LocalDate start, LocalDate end);
	public BlockIndex createNewIndexForBlock(Block block, String fileName, Long startFromByte);
	public BlockIndex createNewIndexForBlock(BlockIndex blockIndex);
}
