package bartoszzychal.BlockChainAnalyze.blockchainreader;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import bartoszzychal.BlockChainAnalyze.index.persistance.BlockIndex;
import bartoszzychal.BlockChainAnalyze.index.persistance.Transaction;
import bartoszzychal.BlockChainAnalyze.model.TransactionSearchInfo;

public interface IBlockChainIndexReader {
	List<BlockIndex> readBlockIndexFromTo(LocalDate from, LocalDate to, List<TransactionSearchInfo> transactionConnections);
	BlockIndex readIndex(String blockHash);
	List<Transaction> readTransactionsFromTo(LocalDateTime from, LocalDateTime to,
			List<TransactionSearchInfo> transactionSearchInfos);
}
