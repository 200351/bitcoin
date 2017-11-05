package bartoszzychal.BlockChainAnalyze.blockchainreader;

import java.time.LocalDate;
import java.util.List;

import org.bitcoinj.core.Block;

public interface IBlockChainReader {
	List<Block> readBlockChainFromTo(LocalDate from, LocalDate to);
}
