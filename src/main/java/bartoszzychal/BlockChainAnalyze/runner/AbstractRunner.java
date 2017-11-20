package bartoszzychal.BlockChainAnalyze.runner;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;
import java.util.stream.Collectors;

import org.apache.commons.io.IOUtils;
import org.bitcoinj.core.Sha256Hash;

import bartoszzychal.BlockChainAnalyze.model.TransactionConnection;

public abstract class AbstractRunner {

	protected void writeToFile(Collection<TransactionConnection> tc, long connectionLimit, int sample, int transactionNumber,
			Sha256Hash startTransactionHash) {
		try {
			final File coinsFile = new File(
					"\\coins" + "\\" + sample + "_" + transactionNumber + "_" + startTransactionHash.toString() + ".csv");
			final File timeFile = new File(
					"\\time" + "\\" + sample + "_" + transactionNumber + "_" + startTransactionHash.toString() + ".csv");
			final File fullFile = new File(
					"\\full" + "\\" + sample + "_" + transactionNumber + "_" + startTransactionHash.toString() + ".csv");
			final OutputStream coinsOS = new FileOutputStream(coinsFile);
			final OutputStream timeOS = new FileOutputStream(timeFile);
			final OutputStream fullOS = new FileOutputStream(fullFile);
			tc = tc.stream().sorted((c1,c2) -> c1.getId().compareTo(c2.getId())).collect(Collectors.toList());
			for (TransactionConnection transactionConnection : tc) {
				IOUtils.write(transactionConnection.toRCoinsString() + "\n", coinsOS);
				IOUtils.write(transactionConnection.toRTimeString() + "\n", timeOS);
				IOUtils.write(transactionConnection.toFullString() + "\n", fullOS);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void createIfNotExists(String directoryPath) {
		File baseDirectory = new File(directoryPath);
		if (!baseDirectory.exists()) {
			baseDirectory.mkdir();
		}
	}
	
	protected String prepareDirectories(long connectionLimit, int sample) {
		final String baseDirctory = "D:\\PWR\\mgr\\PracaMagisterska\\R";
		createIfNotExists(baseDirctory);
		final String connectionsDirectory = baseDirctory + "\\" + connectionLimit;
		createIfNotExists(connectionsDirectory);
		final String sampleDirectory = connectionsDirectory + "\\Sample_" + sample;
		createIfNotExists(sampleDirectory);
		final String coinsDirectory = sampleDirectory + "\\coins";
		createIfNotExists(coinsDirectory);
		final String timeDirectory = sampleDirectory + "\\time";
		createIfNotExists(timeDirectory);
		final String fullDirectory = sampleDirectory + "\\full";
		createIfNotExists(fullDirectory);
		return sampleDirectory;
	}
	
	
}
