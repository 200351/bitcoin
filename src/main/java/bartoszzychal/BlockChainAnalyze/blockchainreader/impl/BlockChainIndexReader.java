package bartoszzychal.BlockChainAnalyze.blockchainreader.impl;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.log4j.Logger;

import bartoszzychal.BlockChainAnalyze.blockchainreader.IBlockChainIndexReader;
import bartoszzychal.BlockChainAnalyze.dbconnection.IBitCoinIndexRepository;
import bartoszzychal.BlockChainAnalyze.dbconnection.IRepository;
import bartoszzychal.BlockChainAnalyze.index.persistance.BlockIndex;
import bartoszzychal.BlockChainAnalyze.model.TransactionSearchInfo;

public class BlockChainIndexReader implements IBlockChainIndexReader {

	private IBitCoinIndexRepository repository;

	private static final Logger log = Logger.getLogger(BlockChainIndexReader.class);

	public BlockChainIndexReader(IBitCoinIndexRepository repository) {
		this.repository = repository;
	}

	@Override
	public List<BlockIndex> readBlockIndexFromTo(LocalDate from, LocalDate to,
			List<TransactionSearchInfo> transactionSearchInfos) {
		List<BlockIndex> returnBlocks = new ArrayList<>();
		if (from != null && to != null && !from.isAfter(to)) {
			if (CollectionUtils.isNotEmpty(transactionSearchInfos)) {
				Set<String> outputs = transactionSearchInfos
						.stream()
						.flatMap(tsi -> tsi.getInfo().stream())
						.map(o -> o.getAddress())
						.collect(Collectors.toSet());
				List<List<String>> partitions = ListUtils.partition(new ArrayList<>(outputs), IRepository.IN_LIMIT);
				log.info("Prepared " + partitions + " partitions.");
				int count = 0;
				for (List<String> partition : partitions) {
					count++;
					log.info("Search by " + count + "partition.");
					List<BlockIndex> indexedBlocks = repository.readIndexedBlocks(from, to, partition);
					log.info("Found " + indexedBlocks == null ? 0
							: indexedBlocks.size() + " by " + count + "partition.");
					if (CollectionUtils.isNotEmpty(indexedBlocks)) {
						returnBlocks.addAll(indexedBlocks);
					}
				}

			}
		} else {
			final List<BlockIndex> indexedBlocks = repository.readIndexedBlocks(from, to);
			returnBlocks = indexedBlocks;
		}
		return returnBlocks;
	}

	@Override
	public BlockIndex readIndex(String blockHash) {
		return repository.readIndex(blockHash);
	}
}
