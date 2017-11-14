package bartoszzychal.BlockChainAnalyze.finder;

import java.time.temporal.TemporalField;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.bitcoinj.core.Coin;
import org.bitcoinj.core.Sha256Hash;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.SetUtils;
import org.bitcoinj.core.Block;
import org.bitcoinj.core.Transaction;
import org.bitcoinj.core.TransactionOutput;

import org.apache.log4j.Logger;

import bartoszzychal.BlockChainAnalyze.index.persistance.BlockIndex;
import bartoszzychal.BlockChainAnalyze.mapper.InfoMapper;
import bartoszzychal.BlockChainAnalyze.model.InputInfo;
import bartoszzychal.BlockChainAnalyze.model.OutputInfo;
import bartoszzychal.BlockChainAnalyze.model.TransactionConnection;
import bartoszzychal.BlockChainAnalyze.model.TransactionSearchInfo;
import bartoszzychal.BlockChainAnalyze.utils.Utils;

public class TransactionChecker {
	private static final Logger log = Logger.getLogger(BlocksConnectionFinder.class);

	public static TransactionConnection areTransactionsConnected(final TransactionSearchInfo searchInfo,
			final Transaction transaction, final Block block) {
		
		// create new TransactionConnection based on TransactionSearchInfo
		final TransactionConnection tc = new TransactionConnection(searchInfo);
		
		// get all input addresses
		final Set<InputInfo> inputsInfo = tc.getInputInfo();
		
		//get all TransactionsOutputs from Transaction
		final List<TransactionOutput> outputs = transaction.getOutputs();
		
		// prepare all possible output addresses from TransactionsOutputs
		final Set<OutputInfo> outputsInfo = outputs != null
				? outputs.stream().map(InfoMapper::map).filter(Utils.isNotNull()).collect(Collectors.toSet())
				: SetUtils.EMPTY_SORTED_SET;
		
		// if addresses exists
		if (CollectionUtils.isNotEmpty(inputsInfo) && CollectionUtils.isNotEmpty(outputsInfo)) {
			final Set<OutputInfo> connection = new HashSet<>(outputsInfo);
			connection.retainAll(inputsInfo);
			// and it's at least one mutual address
			if (connection.size() > 0) {
				tc.setConnectedInfo(connection.stream().map(InfoMapper::map).collect(Collectors.toSet()));
				tc.setOutputInfo(outputsInfo);
				tc.setOutputTransactionHash(transaction.getHash());
				tc.setOutputBlockHash(block.getHash());
				tc.setOutputTime(block.getTimeSeconds());
				log.debug("Found connection between input transactions: " + tc.getInputTransactionHash());
				log.debug("Found connection between output transactions: " + tc.getOutputTransactionHash());
				log.debug("Found connection with " + tc.getConnectedInfo().size() + " Addresses: "
						+ tc.getConnectedInfo().stream().map(c -> c.getAddress()).reduce((a1, a2) -> a1 + ", " + a2).get());
			}
		}
		
		return tc;
	}
	
	public static TransactionConnection areTransactionsConnected(final TransactionSearchInfo searchInfo,
			final bartoszzychal.BlockChainAnalyze.index.persistance.Transaction transaction, final BlockIndex block) {
		
		// create new TransactionConnection based on TransactionSearchInfo
		final TransactionConnection tc = new TransactionConnection(searchInfo);
		
		// get all input addresses
		final Set<InputInfo> inputsInfo = tc.getInputInfo();
		
		//get all TransactionsOutputs from Transaction
		final Set<bartoszzychal.BlockChainAnalyze.index.persistance.TransactionOutput> outputs = transaction.getOutputs();
		
		// prepare all possible output addresses from TransactionsOutputs
		final Set<OutputInfo> outputsInfo = outputs != null ? outputs.stream()
				.map(o -> new OutputInfo(Coin.valueOf(o.getCoins()), o.getAddress())).collect(Collectors.toSet())
				: new HashSet<>();
				
				// if addresses exists
		if (CollectionUtils.isNotEmpty(inputsInfo) && CollectionUtils.isNotEmpty(outputsInfo)) {
			final Set<OutputInfo> connection = new HashSet<>(outputsInfo);
			connection.retainAll(inputsInfo);
			// and it's at least one mutual address
			if (connection.size() > 0) {
				tc.setConnectedInfo(connection.stream().map(InfoMapper::map).collect(Collectors.toSet()));
				tc.setOutputInfo(outputsInfo);
				tc.setOutputTransactionHash(Sha256Hash.wrap(transaction.getTransactionHash()));
				tc.setOutputBlockHash(Sha256Hash.wrap(block.getBlockHash()));
				log.debug("Found connection between input transactions: " + tc.getInputTransactionHash());
				log.debug("Found connection between output transactions: " + tc.getOutputTransactionHash());
				log.debug("Found connection with " + tc.getConnectedInfo().size() + " Addresses: "
						+ tc.getConnectedInfo().stream().map(c -> c.getAddress()).reduce((a1, a2) -> a1 + ", " + a2).get());
			}
		}
				
		return tc;
	}



}
