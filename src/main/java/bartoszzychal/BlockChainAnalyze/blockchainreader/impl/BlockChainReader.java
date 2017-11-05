package bartoszzychal.BlockChainAnalyze.blockchainreader.impl;

import java.time.LocalDate;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import org.bitcoinj.core.Block;
import org.bitcoinj.core.Context;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.core.PrunedException;
import org.bitcoinj.core.VerificationException;
import org.bitcoinj.params.MainNetParams;
import org.bitcoinj.store.BlockStoreException;
import org.bitcoinj.utils.BlockFileLoader;

import bartoszzychal.BlockChainAnalyze.blockchainreader.IBlockChainReader;
import bartoszzychal.BlockChainAnalyze.fileloader.FileLoader;

public class BlockChainReader extends AbstractBlockChainReader implements IBlockChainReader {

	final static NetworkParameters np = new MainNetParams();
	final static Context context = new Context(np);
	private static IBlockChainReader blockChainReverseReader = null;

	public BlockChainReader(Iterator<Block> blockFileLoader) {
		super(blockFileLoader);
	}

	public BlockChainReader() {
		this(new BlockFileLoader(np, FileLoader.readFiles()));
	}

	public static IBlockChainReader getInstance() {
		if (blockChainReverseReader == null) {
			blockChainReverseReader = new BlockChainReader();
		}
		return blockChainReverseReader;
	}

	public static void startAgain() throws VerificationException, BlockStoreException, PrunedException {
		blockChainReverseReader = new BlockChainReverseReader();
	}

	public List<Block> readBlockChainFromTo(LocalDate from, LocalDate to) {

		final LocalDate fromRange = from.minusDays(1);
		final LocalDate toRange = to.plusDays(1);

		// add last not saved block
		if (blockFromPrevious != null) {
			final LocalDate date = parse(blockFromPrevious.getTime());
			addToMap(blockFromPrevious, date);
			blockFromPrevious = null;
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
			System.out.println(dateFromBlock);
			System.out.println("Hash: " + block.getHashAsString());
			System.out.println("PrevHash: " + block.getPrevBlockHash().toString());
			if (dateFromBlock.isAfter(fromRange) && dateFromBlock.isBefore(toRange)) {
				addToMap(block, dateFromBlock);
				counter++;
			} else if (dateFromBlock.isAfter(to)) {
				blockFromPrevious = block;
				end = true;
			}
		}
		final List<Block> result = blockMap.values().parallelStream().flatMap(List::stream)
				.collect(Collectors.toList());
		int countOfFound = result.size();
		System.out.println("Found " + countOfFound + " blocks from date: " + from + " to date: " + to);
		System.out.println("Blocks from cache " + (countOfFound - counter) + ".");
		System.out.println("Readed " + counter + " blocks from BlockChain.");

		return result;
	}
}
