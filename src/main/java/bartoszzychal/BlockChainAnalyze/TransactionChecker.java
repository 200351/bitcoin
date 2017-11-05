package bartoszzychal.BlockChainAnalyze;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.SetUtils;
import org.bitcoinj.core.Address;
import org.bitcoinj.core.Block;
import org.bitcoinj.core.Transaction;
import org.bitcoinj.core.TransactionOutput;
import org.bitcoinj.params.MainNetParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bartoszzychal.BlockChainAnalyze.model.TransactionConnection;
import bartoszzychal.BlockChainAnalyze.model.TransactionSearchInfo;

public class TransactionChecker {
	private static final Logger log = LoggerFactory.getLogger(BlocksConnectionFinder.class);

	public static TransactionConnection areTransactionsConnected(final TransactionSearchInfo searchInfo,
			final Transaction transaction, final Block block) {
		
		// create new TransactionConnection based on TransactionSearchInfo
		final TransactionConnection tc = new TransactionConnection(searchInfo);
		
		// get all input addresses
		final Set<String> inputsAddresses = searchInfo.getAddresses();
		
		//get all TransactionsOutputs from Transaction
		final List<TransactionOutput> outputs = transaction.getOutputs();
		
		// prepare all possible output addresses from TransactionsOutputs
		final Set<String> outputsAddresses = outputs != null ? outputs.stream().map(t -> {
			final Address address = t.getAddressFromP2PKHScript(MainNetParams.get());
			return address != null ? address.toString() : null;
		}).filter(a -> a != null).collect(Collectors.toSet()) : SetUtils.EMPTY_SORTED_SET;
		
		// if addresses exists
		if (CollectionUtils.isNotEmpty(inputsAddresses) && CollectionUtils.isNotEmpty(outputsAddresses)) {
			final Set<String> connection = new HashSet<>(inputsAddresses);
			connection.retainAll(outputsAddresses);
			// and it's at least one mutual address
			if (connection.size() > 0) {
				tc.setConnectedAddresses(connection);
				tc.setOutputAddresses(outputsAddresses);
				tc.setOutputTransactionHash(transaction.getHash());
				tc.setOutputBlockHash(block.getHash());
				tc.setOutputTime(block.getTimeSeconds());
				log.info("Found connection between input transactions: " + tc.getInputTransactionHash());
				log.info("Found connection between output transactions: " + tc.getOutputTransactionHash());
				log.info("Found connection with " + tc.getConnectedAddresses().size() + " Addresses: " + tc.getConnectedAddresses());
			}
		}
		
		return tc;
	}
}
