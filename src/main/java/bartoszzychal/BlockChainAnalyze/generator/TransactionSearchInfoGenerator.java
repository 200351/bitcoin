package bartoszzychal.BlockChainAnalyze.generator;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;
import org.bitcoinj.core.Block;
import org.bitcoinj.core.Coin;
import org.bitcoinj.core.Sha256Hash;
import org.bitcoinj.core.Transaction;
import org.bitcoinj.core.TransactionInput;

import bartoszzychal.BlockChainAnalyze.index.persistance.BlockIndex;
import bartoszzychal.BlockChainAnalyze.mapper.InfoMapper;
import bartoszzychal.BlockChainAnalyze.model.InputInfo;
import bartoszzychal.BlockChainAnalyze.model.TransactionSearchInfo;
import bartoszzychal.BlockChainAnalyze.utils.Utils;

public class TransactionSearchInfoGenerator {
	private static final Logger log = Logger.getLogger(TransactionSearchInfoGenerator.class);

	public static TransactionSearchInfo generate(Transaction transaction, Block block) {
		TransactionSearchInfo searchInfo = new TransactionSearchInfo();
		List<TransactionInput> inputs = transaction.getInputs();
		searchInfo.setInfo(inputs.stream().filter(Utils.isNotCoinBase()).map(InfoMapper::map).filter(Utils.isNotNull())
				.collect(Collectors.toSet()));
		searchInfo.setTransactionHash(transaction.getHash());
		searchInfo.setTime(block.getTimeSeconds());
		searchInfo.setBlockHash(block.getHash());
		return searchInfo;
	}

	public static TransactionSearchInfo generate(
			bartoszzychal.BlockChainAnalyze.index.persistance.Transaction startTransaction, BlockIndex startIndex) {
		TransactionSearchInfo searchInfo = new TransactionSearchInfo();
		Set<bartoszzychal.BlockChainAnalyze.index.persistance.TransactionInput> inputs = startTransaction.getInputs();
		searchInfo.setInfo(inputs.stream().map(i -> new InputInfo(Coin.valueOf(i.getCoins()), i.getAddress())).collect(Collectors.toSet()));
		searchInfo.setTransactionHash(Sha256Hash.wrap(startTransaction.getTransactionHash()));
		searchInfo.setBlockHash(Sha256Hash.wrap(startIndex.getBlockHash()));
		return searchInfo;
	}

}
