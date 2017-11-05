package bartoszzychal.BlockChainAnalyze.fileloader;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bartoszzychal.BlockChainAnalyze.blockfileloader.BlockFileReverseLoader;

public class FileLoader {
	
	private static final String DIR = "D:/PWR/mgr/Praca Magisterska/BitCoinCore/BitcoinCoreInstall/blocks_t/";
	private static final Logger log = LoggerFactory.getLogger(FileLoader.class);

	public static List<File> readFiles() {
		final List<File> files = new ArrayList<>();
		log.info("Read files from directory: " + DIR);
		try (DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get(DIR),
				path -> path.toFile().isFile() && path.toFile().getName().contains("blk"))) {
		    for (Path entry: stream) {
		    	File file = entry.toFile();
		    	if (file != null) {
		    		files.add(file);
		    	}
		    }
		} catch (IOException x) {
		    System.err.println(x);
		}
		return files;
	}
}
