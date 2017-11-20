package bartoszzychal.BlockChainAnalyze.utils;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.TimeZone;
import java.util.function.Predicate;

import org.apache.log4j.Logger;
import org.bitcoinj.core.TransactionInput;

import bartoszzychal.BlockChainAnalyze.model.AbstractInfo;

public class Utils {
	
	private static final Logger log = Logger.getLogger(Utils.class);

	
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
	
	public static void waitForAllThreads(final List<Thread> threads) {
		for (Thread thread : threads) {
			try {
				thread.join();
			} catch (InterruptedException e) {
				log.error("Thread was innterupted: " + e.getStackTrace());
			}
		}
	}

}
