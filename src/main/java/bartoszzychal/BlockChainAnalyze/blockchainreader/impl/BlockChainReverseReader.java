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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bartoszzychal.BlockChainAnalyze.blockchainreader.IBlockChainReader;
import bartoszzychal.BlockChainAnalyze.blockfileloader.BlockFileReverseLoader;
import bartoszzychal.BlockChainAnalyze.fileloader.FileLoader;

public class BlockChainReverseReader extends AbstractBlockChainReader implements IBlockChainReader {

	final static NetworkParameters np = new MainNetParams();
	final static Context context = new Context(np);
	private static IBlockChainReader blockChainReverseReader = null;

	private static final Logger log = LoggerFactory.getLogger(BlockChainReverseReader.class);
	
	public BlockChainReverseReader(Iterator<Block> blockFileLoader) {
		super(blockFileLoader);
	}

	public BlockChainReverseReader() {
		this(new BlockFileReverseLoader(np, FileLoader.readFiles()));
	}

	public static IBlockChainReader getInstance() {
		if (blockChainReverseReader == null) {
			blockChainReverseReader = new BlockChainReverseReader();
		}
		return blockChainReverseReader;
	}

	public static void startAgain() throws VerificationException, BlockStoreException, PrunedException {
		blockChainReverseReader = new BlockChainReverseReader();
	}

	public List<Block> readBlockChainFromTo(LocalDate from, LocalDate to) {

		final LocalDate fromRange = from.minusDays(1);
		final LocalDate toRange = to.plusDays(1);

		// reduce the blockMap
		reduceMap(fromRange, toRange);

		int counter = 0;
		// add last not saved block
		if (blockFromPrevious != null) {
			final LocalDate date = parse(blockFromPrevious.getTime());
			if (date.isAfter(fromRange) && date.isBefore(toRange)) {
				addToMap(blockFromPrevious, date);
				counter = read(from, fromRange, toRange);
			}
		} else {
			counter = read(from, fromRange, toRange);
		}

		final List<Block> result = blockMap.values().parallelStream().flatMap(List::stream)
				.collect(Collectors.toList());
		int countOfFound = result.size();
		log.info("Found " + countOfFound + " blocks from date: " + from + " to date: " + to);
		log.info("Blocks from cache " + (countOfFound - counter) + ".");
		log.info("Readed " + counter + " blocks from BlockChain.");

		return result;
	}

	private int read(LocalDate from, final LocalDate fromRange, final LocalDate toRange) {
		// readed all from range
		boolean end = false;

		// while blocks in date range or exists
		// get from global iterator
		int counter = 0;
		while (iterator.hasNext() && !end) {
			final Block block = iterator.next();
			final LocalDate dateFromBlock = parse(block.getTime());
			log.info(dateFromBlock.toString());
			log.info(counter + ".Hash: " + block.getHashAsString());
			log.info(counter + ".PrevHash: " + block.getPrevBlockHash().toString());
			if (dateFromBlock.isAfter(fromRange) && dateFromBlock.isBefore(toRange)) {
				addToMap(block, dateFromBlock);
				counter++;
			} else if (dateFromBlock.isBefore(from)) {
				blockFromPrevious = block;
				end = true;
			}
		}
		return counter;
	}
}
