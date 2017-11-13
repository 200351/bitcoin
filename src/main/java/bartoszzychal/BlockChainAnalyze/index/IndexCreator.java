package bartoszzychal.BlockChainAnalyze.index;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.bitcoinj.params.MainNetParams;

import org.apache.log4j.Logger;

import bartoszzychal.BlockChainAnalyze.dbconnection.IBitCoinIndexRepository;
import bartoszzychal.BlockChainAnalyze.dbconnection.hsql.HsqlBitcoinIndexRepository;
import bartoszzychal.BlockChainAnalyze.dbconnection.impl.EntityManagerProvider;
import bartoszzychal.BlockChainAnalyze.fileloader.FileLoader;
import bartoszzychal.BlockChainAnalyze.index.loader.BlockIndexLoader;
import bartoszzychal.BlockChainAnalyze.index.persistance.BlockIndex;

public class IndexCreator {
	private static final Logger log = Logger.getLogger(IndexCreator.class);

	private IBitCoinIndexRepository repository;

	public IndexCreator(IBitCoinIndexRepository repository) {
		this.repository = repository;
	}

	public void indexing(Integer startFile, Integer endFile) {
		final List<File> files = prepareFilesToIndexing(startFile, endFile);
		int count = 0;
		if (CollectionUtils.isNotEmpty(files)) {
			final BlockIndexLoader blockIndexLoader = new BlockIndexLoader(MainNetParams.get(), files);
			while (blockIndexLoader.hasNext()) {
				final BlockIndex index = blockIndexLoader.next();
				if (index != null) {
					final BlockIndex newIndex = repository.createNewIndexForBlock(index, false);
					count = newIndex == null ? count : count + 1;
					log.info("Generated " + count + " Index. " + newIndex.getBlockHash());
				}
			}
		}
		EntityManagerProvider.closeConnection();
	}

	private List<File> prepareFilesToIndexing(Integer startFile, Integer endFile) {
		return FileLoader.readFiles().stream().filter(file -> {
			final String fileName = file.getName().replaceAll("blk", "").replaceAll(".dat", "");
			if (StringUtils.isNumeric(fileName)) {
				final Integer fileNameNumber = Integer.valueOf(fileName);
				if (fileNameNumber >= startFile && fileNameNumber <= endFile) {
					return true;
				}
			}
			return false;
		}).collect(Collectors.toList());
	}

	public static void main(String[] args) {
		FileLoader.setDir("D:/PWR/mgr/PracaMagisterska/BitCoinCore/BitcoinCoreInstall/blocks/");
		final IndexCreator indexCreator = new IndexCreator(new HsqlBitcoinIndexRepository());
//		indexCreator.indexing(0, 50);
//		indexCreator.indexing(51, 100);
//		indexCreator.indexing(101, 150);
//		indexCreator.indexing(151, 200);
//		indexCreator.indexing(201, 250);
//		indexCreator.indexing(251, 300);
//		indexCreator.indexing(301, 350);
//		indexCreator.indexing(351, 400);
//		indexCreator.indexing(401, 450);
//		indexCreator.indexing(451, 500);
//		indexCreator.indexing(501, 550);
//		indexCreator.indexing(551, 600);
//		indexCreator.indexing(601, 650);
//		indexCreator.indexing(651, 700);
//		indexCreator.indexing(701, 750);
//		indexCreator.indexing(751, 800);
//		indexCreator.indexing(801, 850);
//		indexCreator.indexing(851, 900);
//		indexCreator.indexing(901, 950);
//		indexCreator.indexing(951, 975);
//
	}
}
