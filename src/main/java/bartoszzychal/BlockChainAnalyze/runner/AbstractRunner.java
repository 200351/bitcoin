package bartoszzychal.BlockChainAnalyze.runner;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.bitcoinj.core.Sha256Hash;

import bartoszzychal.BlockChainAnalyze.model.TransactionConnection;

public abstract class AbstractRunner {

	protected void writeToFile(List<TransactionConnection> tc, long connectionLimit, int sample, int transactionNumber,
			Sha256Hash startTransactionHash) {
		try {
			final String baseDirctory = "D:\\PWR\\mgr\\Praca Magisterska\\R";
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
			final File coinsFile = new File(
					coinsDirectory+ "\\" + sample + "_" + transactionNumber + "_" + startTransactionHash.toString() + ".csv");
			final File timeFile = new File(
					timeDirectory + "\\" + sample + "_" + transactionNumber + "_" + startTransactionHash.toString() + ".csv");
			final File fullFile = new File(
					fullDirectory + "\\" + sample + "_" + transactionNumber + "_" + startTransactionHash.toString() + ".csv");
			final OutputStream coinsOS = new FileOutputStream(coinsFile);
			final OutputStream timeOS = new FileOutputStream(timeFile);
			final OutputStream fullOS = new FileOutputStream(fullFile);
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
}
