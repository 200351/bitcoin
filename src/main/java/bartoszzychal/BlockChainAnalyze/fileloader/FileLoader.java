package bartoszzychal.BlockChainAnalyze.fileloader;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;


import org.apache.log4j.Logger;

public class FileLoader {
	
	private static String dir = "D:/PWR/mgr/PracaMagisterska/BitCoinCore/BitcoinCoreInstall/blocks/";
	private static final Logger log = Logger.getLogger(FileLoader.class);

	public static List<File> readFiles() {
		final List<File> files = new ArrayList<>();
		log.info("Read files from directory: " + dir);
		try (DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get(dir),
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
	
	public static void setDir(String path) {
		dir = path;
	}
}
