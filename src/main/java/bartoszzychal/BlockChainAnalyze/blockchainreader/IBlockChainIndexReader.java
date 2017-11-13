package bartoszzychal.BlockChainAnalyze.blockchainreader;

import java.time.LocalDate;
import java.util.List;

import org.bitcoinj.core.Block;

import bartoszzychal.BlockChainAnalyze.index.persistance.BlockIndex;
import bartoszzychal.BlockChainAnalyze.model.TransactionConnection;

public interface IBlockChainIndexReader {
	List<BlockIndex> readBlockChainFromTo(LocalDate from, LocalDate to, List<TransactionConnection> transactionConnections);
}
