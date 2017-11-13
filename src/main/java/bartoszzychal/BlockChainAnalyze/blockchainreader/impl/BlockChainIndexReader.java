package bartoszzychal.BlockChainAnalyze.blockchainreader.impl;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.log4j.Logger;

import bartoszzychal.BlockChainAnalyze.blockchainreader.IBlockChainIndexReader;
import bartoszzychal.BlockChainAnalyze.dbconnection.IBitCoinIndexRepository;
import bartoszzychal.BlockChainAnalyze.index.persistance.BlockIndex;
import bartoszzychal.BlockChainAnalyze.model.OutputInfo;
import bartoszzychal.BlockChainAnalyze.model.TransactionConnection;

public class BlockChainIndexReader implements IBlockChainIndexReader {

	private static final int IN_LIMIT = 1000;
	private IBitCoinIndexRepository repository;
	private static final Logger log = Logger.getLogger(BlockChainIndexReader.class);

	public BlockChainIndexReader(IBitCoinIndexRepository repository) {
		this.repository = repository;
	}

	@Override
	public List<BlockIndex> readBlockChainFromTo(LocalDate from, LocalDate to,
			List<TransactionConnection> transactionConnections) {
		List<BlockIndex> returnBlocks = new ArrayList<>();
		if (from != null && to != null && !from.isAfter(to)) {
			if (CollectionUtils.isNotEmpty(transactionConnections)) {
				List<OutputInfo> outputs = transactionConnections.stream().flatMap(tc -> tc.getOutputInfo().stream())
						.collect(Collectors.toList());
				Set<String> addresses = outputs.stream().map(o -> o.getAddress()).collect(Collectors.toSet());
				List<String> addressesList = new ArrayList<>(addresses);
				List<List<String>> partitions = ListUtils.partition(addressesList, IN_LIMIT);
				for (List<String> partition : partitions) {
					List<BlockIndex> indexedBlocks = repository.readIndexedBlocks(from, to, partition);
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

}
