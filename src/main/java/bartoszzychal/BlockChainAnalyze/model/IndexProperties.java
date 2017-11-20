package bartoszzychal.BlockChainAnalyze.model;

import bartoszzychal.BlockChainAnalyze.model.IndexProperties.Level;

public class IndexProperties {

	public static enum Level {
		BLOCK_LEVEL, TRANSACTION_LEVEL, TRANSACTION_DETAILS_LEVEL 
	}

	private static Level level = Level.BLOCK_LEVEL;

	public static Level getLevel() {
		return level;
	}

	public static void setLevel(Level level) {
		IndexProperties.level = level;
	}
	
	public static boolean isIndexTransaction() {
		return Level.TRANSACTION_LEVEL.equals(IndexProperties.getLevel()) || isIndexTransactionDetails();
	}

	public static boolean isIndexTransactionDetails() {
		return Level.TRANSACTION_DETAILS_LEVEL.equals(IndexProperties.getLevel());
	}
	
}
