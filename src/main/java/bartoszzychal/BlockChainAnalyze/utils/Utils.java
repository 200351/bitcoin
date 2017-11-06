package bartoszzychal.BlockChainAnalyze.utils;

import java.util.function.Predicate;

import org.bitcoinj.core.TransactionInput;

import bartoszzychal.BlockChainAnalyze.model.AbstractInfo;

public class Utils {
	public static Predicate<? super AbstractInfo> isNotNull() {
		return a -> a != null;
	}

	public static Predicate<? super TransactionInput> isNotCoinBase() {
		return t -> !t.isCoinBase();
	}
}
