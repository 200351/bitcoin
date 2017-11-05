package bartoszzychal.BlockChainAnalyze.generator;

import java.util.List;
import java.util.stream.Collectors;

import org.bitcoinj.core.Address;
import org.bitcoinj.core.Block;
import org.bitcoinj.core.Transaction;
import org.bitcoinj.core.TransactionInput;
import org.bitcoinj.script.Script;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bartoszzychal.BlockChainAnalyze.model.TransactionSearchInfo;

public class TransactionSearchInfoGenerator {
	private static final Logger log = LoggerFactory.getLogger(TransactionSearchInfoGenerator.class);

	public static TransactionSearchInfo generate(Transaction transaction, Block block) {
		TransactionSearchInfo transactionConnection = new TransactionSearchInfo();
		List<TransactionInput> inputs = transaction.getInputs();
		transactionConnection.setAddresses(inputs.stream().filter(t -> !t.isCoinBase()).map(t -> {
			Address fromAddress = null;
			try {
				final Script scriptSig = t.getScriptSig();
				final boolean chunkSize = (scriptSig.getChunks().size() == 2);
				if (chunkSize) {
					fromAddress = t.getFromAddress();
				} else {
					log.info("Chunk size too big.");
				}
			} catch (Exception e) {
				log.info("From Address can not be calculate.");
			}
			return fromAddress != null ? fromAddress.toString() : null;
		}).filter(a -> a != null).collect(Collectors.toSet()));
		transactionConnection.setTransactionHash(transaction.getHash());
		transactionConnection.setTime(block.getTimeSeconds());
		transactionConnection.setBlockHash(block.getHash());
		return transactionConnection;
	}
}
