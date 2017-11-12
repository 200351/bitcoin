package bartoszzychal.BlockChainAnalyze.utils;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.TimeZone;
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
	
	public static LocalDateTime parse(long time) {
		return LocalDateTime.ofInstant(Instant.ofEpochSecond(time), 
                TimeZone.getTimeZone("GMT").toZoneId());  
	}
}
