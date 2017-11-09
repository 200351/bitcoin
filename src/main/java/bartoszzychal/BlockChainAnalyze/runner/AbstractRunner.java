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

	protected void writeToFile(List<TransactionConnection> tc, int connectionLimit, int sample,
			Sha256Hash startTransactionHash) {
		try {
			createIfNotExists("D:\\PWR\\mgr\\Praca Magisterska\\R");
			String sampleDirectoryPath = "D:\\PWR\\mgr\\Praca Magisterska\\R\\Sample" + sample;
			createIfNotExists(sampleDirectoryPath);
			final File coinsFile = new File(
					sampleDirectoryPath + "\\coins" + sample + "_" + startTransactionHash.toString() + ".csv");
			final File timeFile = new File(
					sampleDirectoryPath + "\\time" + sample + "_" + startTransactionHash.toString() + ".csv");
			final OutputStream coinsOS = new FileOutputStream(coinsFile);
			final OutputStream timeOS = new FileOutputStream(timeFile);
			for (TransactionConnection transactionConnection : tc) {
				IOUtils.write(transactionConnection.toRCoinsString() + "\n", coinsOS);
				IOUtils.write(transactionConnection.toRTimeString() + "\n", timeOS);
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
