package bartoszzychal.BlockChainAnalyze.index.mapper;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.log4j.Logger;
import org.bitcoinj.core.Address;
import org.bitcoinj.core.Block;
import org.bitcoinj.core.Coin;
import org.bitcoinj.params.MainNetParams;
import org.bitcoinj.script.Script;

import bartoszzychal.BlockChainAnalyze.index.persistance.BlockIndex;
import bartoszzychal.BlockChainAnalyze.index.persistance.Transaction;
import bartoszzychal.BlockChainAnalyze.index.persistance.TransactionInput;
import bartoszzychal.BlockChainAnalyze.index.persistance.TransactionOutput;
import bartoszzychal.BlockChainAnalyze.utils.Utils;

public class PersistanceMapper {
	private static final Logger log = Logger.getLogger(PersistanceMapper.class);

	public static BlockIndex map(Block block) {
		final BlockIndex blockIndex = new BlockIndex();
		blockIndex.setBlockHash(block.getHashAsString());
		final LocalDateTime parse = Utils.parse(block.getTimeSeconds());
		blockIndex.setGeneratedDate(parse);
		if (CollectionUtils.isNotEmpty(block.getTransactions())) {
			blockIndex.setTransactions(block.getTransactions().parallelStream().map(PersistanceMapper::map).collect(Collectors.toSet()));
		}
		return blockIndex;
	}
	
	public static Transaction map(org.bitcoinj.core.Transaction transactionToMap) {
		final Transaction transaction = new Transaction();
		transaction.setInputSum(transactionToMap.getInputSum().getValue());
		transaction.setOutputSum(transactionToMap.getOutputSum().getValue());
		transaction.setTransactionHash(transactionToMap.getHashAsString());
		if (CollectionUtils.isNotEmpty(transactionToMap.getInputs())) {
			transaction.setInputs(
					transactionToMap
					.getInputs()
					.parallelStream()
					.map(PersistanceMapper::map)
					.filter(to -> to != null)
					.collect(Collectors.toSet())
					);
		}
		if (CollectionUtils.isNotEmpty(transactionToMap.getOutputs())) {
			transaction.setOutputs(
					transactionToMap
					.getOutputs()
					.parallelStream()
					.map(PersistanceMapper::map)
					.filter(to -> to != null)
					.collect(Collectors.toSet())
					);
		}
		return transaction;
	}
	
	public static TransactionOutput map(org.bitcoinj.core.TransactionOutput outputToMap) {
		final TransactionOutput transactionOutput = new TransactionOutput();
		Address address = null;
		try {
			address = outputToMap.getAddressFromP2PKHScript(MainNetParams.get());
		} catch (Exception e) {
			try {
				address = outputToMap.getAddressFromP2SH(MainNetParams.get());
			} catch (Exception e2) {
				address = null;
			}
		}
		final Coin value = outputToMap.getValue();
		if (address == null || value == null) {
			return null;
		}
		transactionOutput.setAddress(address.toString());
		transactionOutput.setCoins(value.getValue());
		return transactionOutput;
	}
	
	public static TransactionInput map(org.bitcoinj.core.TransactionInput inputToMap) {
		final TransactionInput transactionInput = new TransactionInput();
		Address address = null;
		try {
			final Script scriptSig = inputToMap.getScriptSig();
			final boolean chunkSize = (scriptSig.getChunks().size() == 2);
			if (chunkSize) {
				address = inputToMap.getFromAddress();
			} else {
				log.warn("Chunk size too big.");
			}
		} catch (Exception e) {
			log.warn("From Address can not be calculate.");
		}
		if (address == null) {
			return null;
		}
		transactionInput.setAddress(address.toString());
		return transactionInput;
	}
}
