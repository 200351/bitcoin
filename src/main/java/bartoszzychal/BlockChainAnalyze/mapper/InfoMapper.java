package bartoszzychal.BlockChainAnalyze.mapper;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.bitcoinj.core.Address;
import org.bitcoinj.core.TransactionInput;
import org.bitcoinj.core.TransactionOutput;
import org.bitcoinj.params.MainNetParams;
import org.bitcoinj.script.Script;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bartoszzychal.BlockChainAnalyze.model.AbstractInfo;
import bartoszzychal.BlockChainAnalyze.model.ConnectedInfo;
import bartoszzychal.BlockChainAnalyze.model.InputInfo;
import bartoszzychal.BlockChainAnalyze.model.OutputInfo;
import bartoszzychal.BlockChainAnalyze.model.TransactionSearchInfo;

public class InfoMapper {
	
	private static final Logger log = LoggerFactory.getLogger(InfoMapper.class);
	
	public static Set<InputInfo> map(TransactionSearchInfo tc) {
		if (tc != null && CollectionUtils.isNotEmpty(tc.getInfo())) {
			return tc.getInfo().stream().map(i -> new InputInfo(i.getCoins(), i.getAddress())).collect(Collectors.toSet());
		}
		return new HashSet<>();
	}

	public static InputInfo map(TransactionInput t) {
		Address fromAddress = null;
		try {
			final Script scriptSig = t.getScriptSig();
			final boolean chunkSize = (scriptSig.getChunks().size() == 2);
			if (chunkSize) {
				fromAddress = t.getFromAddress();
			} else {
				log.warn("Chunk size too big.");
			}
		} catch (Exception e) {
			log.warn("From Address can not be calculate.");
		}
		return fromAddress != null ? new InputInfo(t.getValue(), fromAddress.toString()) : null;
	}

	public static OutputInfo map(TransactionOutput t) {
		Address address = null;
		try {
			address = t.getAddressFromP2PKHScript(MainNetParams.get());
		} catch (Exception e) {
			try {
				address = t.getAddressFromP2SH(MainNetParams.get());
			} catch (Exception e2) {
				address = null;
			}

		}
		return address != null ? new OutputInfo(t.getValue(), address.toString()) : null;
	}
	
	public static ConnectedInfo map(AbstractInfo info) {
		return new ConnectedInfo(info.getCoins(), info.getAddress());
	}
	
}
