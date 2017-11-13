package bartoszzychal.BlockChainAnalyze.generator;

import java.util.List;
import java.util.stream.Collectors;

import org.bitcoinj.core.Block;
import org.bitcoinj.core.Transaction;
import org.bitcoinj.core.TransactionInput;

import org.apache.log4j.Logger;

import bartoszzychal.BlockChainAnalyze.mapper.InfoMapper;
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

}
