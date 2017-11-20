package bartoszzychal.BlockChainAnalyze.index;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.bitcoinj.params.MainNetParams;

import bartoszzychal.BlockChainAnalyze.dbconnection.IBitCoinIndexRepository;
import bartoszzychal.BlockChainAnalyze.dbconnection.IRepository;
import bartoszzychal.BlockChainAnalyze.dbconnection.hsql.HsqlBitcoinIndexRepository;
import bartoszzychal.BlockChainAnalyze.fileloader.FileLoader;
import bartoszzychal.BlockChainAnalyze.index.loader.BlockIndexLoader;
import bartoszzychal.BlockChainAnalyze.index.persistance.BlockIndex;
import bartoszzychal.BlockChainAnalyze.model.IndexProperties;
import bartoszzychal.BlockChainAnalyze.model.IndexProperties.Level;

public class IndexCreator implements Runnable {
	private static final Logger log = Logger.getLogger(IndexCreator.class);

	private IBitCoinIndexRepository repository;
	private Integer startFile;
	private Integer endFile;
	private static LinkedList<File> files;
	
	
	public IndexCreator(IBitCoinIndexRepository repository) {
		this.repository = repository;
	}

	public void indexing(Integer startFile, Integer endFile) {
		final List<File> files = prepareFilesToIndexing(startFile, endFile);
		int count = 0;
		log.info("Start");
		if (CollectionUtils.isNotEmpty(files)) {
			final BlockIndexLoader blockIndexLoader = new BlockIndexLoader(MainNetParams.get(), files);
			final List<BlockIndex> indexesToPersist = new ArrayList<>();
			while (blockIndexLoader.hasNext()) {
				final BlockIndex index = blockIndexLoader.next();
				if (index != null) {
					indexesToPersist.add(index);
					count++;
				}
				if (count % IRepository.COLLECTION_SIZE == 0) {
					if (CollectionUtils.isNotEmpty(indexesToPersist)) {
						repository.createNewIndexForBlock(indexesToPersist, false);
						log.info("Generated " + count + " Index. ");
						indexesToPersist.clear();
					}
				}
			}
			
		}
		log.info("Success End.");
	}

	public void indexing(File file) {
		int count = 0;
		log.info("Start " + file.getName());
		final List<File> files = new ArrayList<>();
		if (file != null) {
			files.add(file);
			final BlockIndexLoader blockIndexLoader = new BlockIndexLoader(MainNetParams.get(), files);
			final List<BlockIndex> indexesToPersist = new ArrayList<>();
			while (blockIndexLoader.hasNext()) {
				final BlockIndex index = blockIndexLoader.next();
				if (index != null) {
					indexesToPersist.add(index);
					count++;
				}
				if (count % IRepository.COLLECTION_SIZE == 0 || !blockIndexLoader.hasNext()) {
					if (CollectionUtils.isNotEmpty(indexesToPersist)) {
						repository.createNewIndexForBlock(indexesToPersist, false);
						log.info("Generated " + count + " Index. ");
						indexesToPersist.clear();
					}
				}
			}
			
		}
		log.info("Success End " + file.getName());
	}

	public static List<File> prepareFilesToIndexing(Integer startFile, Integer endFile) {
		return Collections.synchronizedList(FileLoader.readFiles().stream().filter(file -> {
			final String fileName = file.getName().replaceAll("blk", "").replaceAll(".dat", "");
			if (StringUtils.isNumeric(fileName)) {
				final Integer fileNameNumber = Integer.valueOf(fileName);
				if (fileNameNumber >= startFile && fileNameNumber <= endFile) {
					return true;
				}
			}
			return false;
		}).collect(Collectors.toList()));
	}

	public static void prepareConcurenceFilesToIndexing(Integer startFile, Integer endFile) {
		files = new LinkedList<>(FileLoader.readFiles().stream().filter(file -> {
			final String fileName = file.getName().replaceAll("blk", "").replaceAll(".dat", "");
			if (StringUtils.isNumeric(fileName)) {
				final Integer fileNameNumber = Integer.valueOf(fileName);
				if (fileNameNumber >= startFile && fileNameNumber <= endFile) {
					return true;
				}
			}
			return false;
		}).collect(Collectors.toList()));
	}
	
	private synchronized File getFileToIndexing() {
		File first = null;
		synchronized (files) {
			if (CollectionUtils.isNotEmpty(files)) {
				first = files.getFirst();
				files.removeFirst();
			}
		}
		return first;
	}

	@Override
	public void run() {
		File file = getFileToIndexing();
		while(file != null) {
			synchronized (files) {
				indexing(file);
				file = getFileToIndexing();
			}
		}
	}
	
	public static void main(String[] args) {
		FileLoader.setDir("D:/PWR/mgr/PracaMagisterska/BitCoinCore/BitcoinCoreInstall/blocks/");
		IndexCreator.prepareConcurenceFilesToIndexing(0, 975);
		IndexProperties.setLevel(Level.BLOCK_LEVEL);
		
		int nodes = 30;
		for (int i = 0; i < nodes; i++) {
			final IndexCreator indexCreator = new IndexCreator(new HsqlBitcoinIndexRepository());
			final Thread thread = new Thread(indexCreator);
			thread.setUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
		         public void uncaughtException(Thread t, Throwable e) {
		            log.info(t + " throws exception: " + e);
		         }
		      });
			thread.start();
		}
	}

	public Integer getStartFile() {
		return startFile;
	}

	public void setStartFile(Integer startFile) {
		this.startFile = startFile;
	}

	public Integer getEndFile() {
		return endFile;
	}

	public void setEndFile(Integer endFile) {
		this.endFile = endFile;
	}

}
