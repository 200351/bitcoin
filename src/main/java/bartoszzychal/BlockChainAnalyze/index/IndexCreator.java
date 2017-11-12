package bartoszzychal.BlockChainAnalyze.index;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.bitcoinj.params.MainNetParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bartoszzychal.BlockChainAnalyze.blockfileloader.BlockIndexLoader;
import bartoszzychal.BlockChainAnalyze.dbconnection.IBitCoinIndexRepository;
import bartoszzychal.BlockChainAnalyze.dbconnection.hsql.HsqlBitcoinIndexRepository;
import bartoszzychal.BlockChainAnalyze.dbconnection.impl.EntityManagerProvider;
import bartoszzychal.BlockChainAnalyze.fileloader.FileLoader;
import bartoszzychal.BlockChainAnalyze.persistance.BlockIndex;

public class IndexCreator {
	private static final Logger log = LoggerFactory.getLogger(IndexCreator.class);

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
		FileLoader.setDir("D:/PWR/mgr/Praca Magisterska/BitCoinCore/BitcoinCoreInstall/blocks/");
		final IndexCreator indexCreator = new IndexCreator(new HsqlBitcoinIndexRepository());
//		indexCreator.indexing(100, 250);
//		indexCreator.indexing(250, 500);
//		indexCreator.indexing(500, 750);
		indexCreator.indexing(150, 975);
	}
}
