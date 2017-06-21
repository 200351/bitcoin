package bartoszzychal.BlockChainAnalyze;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.bitcoinj.core.Block;
import org.bitcoinj.core.Context;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.params.MainNetParams;
import org.bitcoinj.utils.BlockFileLoader;

public class BlockChainReader {

	private final String DIR = "D:/PWR/mgr/Praca Magisterska/BitCoinCore/BitcoinCoreInstall/blocks/";
	private static BlockChainReader blockChainReader;
	
	private BlockFileLoader blockFileLoader;
	private Context context;

	private Map<LocalDate, List<Block>> blockMap;
	private Iterator<Block> iterator;
	private Block blockFromNext;
	
	private BlockChainReader() {
		final List<File> files = readFiles();
		final NetworkParameters np = new MainNetParams();
		context  = new Context(np);
		blockFileLoader = new BlockFileLoader(np, files);
		iterator = blockFileLoader.iterator();
		blockMap = new LinkedHashMap<>();
	}
	
	private List<File> readFiles() {
		final List<File> files = new ArrayList<>();
		try (DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get(DIR), path -> path.toFile().isFile())) {
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
	
	public static BlockChainReader getInstance() {
		if (blockChainReader == null) {
			blockChainReader = new BlockChainReader();
		}
		return blockChainReader;
	}
	
	
	public List<Block> readBlockChainFromTo(LocalDate from, LocalDate to) {
		
		final LocalDate fromRange = from.minusDays(1);
		final LocalDate toRange = to.plusDays(1);
		
		// add last not saved block
		if (blockFromNext != null) {
			final LocalDate date = parse(blockFromNext.getTime());
			addToMap(blockFromNext, date);
			blockFromNext = null;
		}
		
		// reduce the blockMap
		reduceMap(fromRange, toRange);
		
		// readed all from range
		boolean end = false;
		
		// while blocks in date range or exists
		// get from global iterator 
		int counter = 0;
		while (iterator.hasNext() && !end) {
			final Block block = iterator.next();
			final LocalDate dateFromBlock = parse(block.getTime());
			if (dateFromBlock.isAfter(fromRange) && dateFromBlock.isBefore(toRange)) {
				addToMap(block, dateFromBlock);
				counter++;
 			} else if (dateFromBlock.isAfter(to)) {
 				blockFromNext = block;
 				end = true;
 			}
		}
		final List<Block> result = blockMap.values().parallelStream().flatMap(List::stream).collect(Collectors.toList());
		int countOfFound = result.size();
		System.out.println("Found " + countOfFound + " blocks from date: " + from + " to date: " + to);
		System.out.println("Blocks from cache " + (countOfFound - counter) + ".");
		System.out.println("Readed " + counter + " blocks from BlockChain.");
		
		return result;
	}

	private LocalDate parse(Date time) {
		return time.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
	}

	private void reduceMap(LocalDate from, LocalDate to) {
		blockMap = blockMap
		.entrySet()
		.parallelStream()
		.filter(entry -> entry.getKey().isAfter(from) && entry.getKey().isBefore(to)).
		collect(LinkedHashMap<LocalDate, List<Block>>::new, 
                (m, c) -> m.put(c.getKey(), c.getValue()),
                (m, u) -> {});
	}

	private void addToMap(final Block block, final LocalDate date) {
		if (blockMap.containsKey(date)) {
			blockMap.get(date).add(block);
		} else {
			blockMap.put(date, new LinkedList<>());
			blockMap.get(date).add(block);
		}
	}
	
}
