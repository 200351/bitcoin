package bartoszzychal.BlockChainAnalyze.blockchainreader.impl;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.bitcoinj.core.Block;

import bartoszzychal.BlockChainAnalyze.blockchainreader.IBlockChainReader;

public abstract class AbstractBlockChainReader implements IBlockChainReader {

	protected Map<LocalDate, List<Block>> blockMap;
	protected Iterator<Block> iterator;
	protected Block blockFromPrevious;
	
	protected AbstractBlockChainReader(Iterator<Block> blockFileLoader) {
		iterator = blockFileLoader;
		blockMap = new LinkedHashMap<>();
	}
		

	protected LocalDate parse(Date time) {
		return time.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
	}

	protected void reduceMap(LocalDate from, LocalDate to) {
		blockMap = blockMap
		.entrySet()
		.parallelStream()
		.filter(entry -> entry.getKey().isAfter(from) && entry.getKey().isBefore(to)).
		collect(LinkedHashMap<LocalDate, List<Block>>::new, 
                (m, c) -> m.put(c.getKey(), c.getValue()),
                (m, u) -> {});
	}

	protected void addToMap(final Block block, final LocalDate date) {
		if (blockMap.containsKey(date)) {
			blockMap.get(date).add(block);
		} else {
			blockMap.put(date, new LinkedList<>());
			blockMap.get(date).add(block);
		}
	}
	
}
