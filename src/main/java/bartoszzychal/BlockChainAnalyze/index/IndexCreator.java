package bartoszzychal.BlockChainAnalyze.index;

import java.io.File;
import java.util.ArrayList;
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

public class IndexCreator implements Runnable {
	private static final Logger log = Logger.getLogger(IndexCreator.class);

	private IBitCoinIndexRepository repository;
	private Integer startFile;
	private Integer endFile;
	
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

	private List<File> prepareFilesToIndexing(Integer startFile, Integer endFile) {
		return FileLoader.readFiles().stream().filter(file -> {
			final String fileName = file.getName().replaceAll("blk", "").replaceAll(".dat", "");
			if (StringUtils.isNumeric(fileName)) {
				final Integer fileNameNumber = Integer.valueOf(fileName);
				if (fileNameNumber >= startFile && fileNameNumber <= endFile && fileNameNumber != 81
						&& fileNameNumber != 82) {
					return true;
				}
			}
			return false;
		}).collect(Collectors.toList());
	}

	@Override
	public void run() {
		indexing(getStartFile(), getEndFile());
	}
	
	public static void main(String[] args) {
		FileLoader.setDir("D:/PWR/mgr/PracaMagisterska/BitCoinCore/BitcoinCoreInstall/blocks/");
		
		int skip = 0;
		for(int i = 55; i < 56; i = i + skip + 1) {
			final IndexCreator indexCreator = new IndexCreator(new HsqlBitcoinIndexRepository());
			indexCreator.setStartFile(Integer.valueOf(i));
			indexCreator.setEndFile(Integer.valueOf(i + skip));
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
