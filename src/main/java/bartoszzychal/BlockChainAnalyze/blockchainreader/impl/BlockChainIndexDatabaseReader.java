package bartoszzychal.BlockChainAnalyze.blockchainreader.impl;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.bitcoinj.core.Block;
import org.bitcoinj.core.Context;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.params.MainNetParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bartoszzychal.BlockChainAnalyze.BlockchainHelper;
import bartoszzychal.BlockChainAnalyze.blockchainreader.IBlockChainReader;
import bartoszzychal.BlockChainAnalyze.blockfileloader.BlockLoader;
import bartoszzychal.BlockChainAnalyze.dbconnection.IBitCoinIndexRepository;
import bartoszzychal.BlockChainAnalyze.fileloader.FileLoader;
import bartoszzychal.BlockChainAnalyze.model.Blockchain;
import bartoszzychal.BlockChainAnalyze.persistance.BlockIndex;

public class BlockChainIndexDatabaseReader implements IBlockChainReader{

	private IBitCoinIndexRepository repository;
	final static NetworkParameters np = new MainNetParams();
	final static Context context = new Context(np);
	final BlockLoader blockLoader;
	private static final Logger log = LoggerFactory.getLogger(BlockChainIndexDatabaseReader.class);
	private Iterator<BlockIndex> indexIt = null;
	private final int BLOCK_COUNT = 30;
	
	public BlockChainIndexDatabaseReader(IBitCoinIndexRepository repository) {
		this.repository = repository;
		this.blockLoader = new BlockLoader(np, FileLoader.readFiles());
	}

	@Override
	public List<Block> readBlockChainFromTo(LocalDate from, LocalDate to) {
		List<Block> returnBlocks = null;
		if (from != null && to != null && !from.isAfter(to)) {
			if (indexIt == null || !indexIt.hasNext()) {
				final List<BlockIndex> index = repository.readIndexedBlocks(from, to);
				indexIt = index.iterator();
			}
			final List<Block> blocks = new ArrayList<>();
			int counter = 0;
			while (indexIt.hasNext()) {
				BlockIndex blockIndex = (BlockIndex) indexIt.next();
				final Block readBlock = blockLoader.readBlock(blockIndex);
				if (readBlock != null) {
					log.info("Readed block: " + readBlock.getHashAsString());
					blocks.add(readBlock);
				}
				indexIt.remove();
				counter++;
				if (counter == BLOCK_COUNT) {
					break;
				}
			}
			log.info("Readed " + blocks.size() + " blocks with batching " + BLOCK_COUNT);
			final Blockchain blockchain = BlockchainHelper.recreateBlockchain(blocks, true, false);
			returnBlocks = blockchain.getBlocks();
		}
		return returnBlocks;
	}

}
