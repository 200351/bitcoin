package bartoszzychal.BlockChainAnalyze.blockchainreader.impl;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.log4j.Logger;
import org.bitcoinj.core.Block;
import org.bitcoinj.utils.ContextPropagatingThreadFactory;

import bartoszzychal.BlockChainAnalyze.blockchain.BlockchainHelper;
import bartoszzychal.BlockChainAnalyze.blockchainreader.IBlockChainReader;
import bartoszzychal.BlockChainAnalyze.blockfileloader.BlockLoader;
import bartoszzychal.BlockChainAnalyze.dbconnection.IBitCoinIndexRepository;
import bartoszzychal.BlockChainAnalyze.fileloader.FileLoader;
import bartoszzychal.BlockChainAnalyze.index.persistance.BlockIndex;
import bartoszzychal.BlockChainAnalyze.model.Blockchain;
import bartoszzychal.BlockChainAnalyze.utils.Utils;

public class BlockChainByIndexReader implements IBlockChainReader{

	private IBitCoinIndexRepository repository;
	final BlockLoader blockLoader;
	private static final Logger log = Logger.getLogger(BlockChainByIndexReader.class);
	private List<BlockIndex> index = null;
	private final int BLOCK_COUNT = 30;
	
	public BlockChainByIndexReader(IBitCoinIndexRepository repository) {
		this.repository = repository;
		this.blockLoader = new BlockLoader(FileLoader.readFiles());
	}

	@Override
	public List<Block> readBlockChainFromTo(LocalDate from, LocalDate to) {
		List<Block> returnBlocks = null;
		if (from != null && to != null && !from.isAfter(to)) {
			if (CollectionUtils.isEmpty(index)) {
				final List<BlockIndex> index = repository.readIndexedBlocks(from, to);
				this.index = index;
			}
			final List<Block> blocks = new ArrayList<>();
			List<BlockIndex> tempIndex = index.stream().limit(BLOCK_COUNT).collect(Collectors.toList());
			index.removeAll(tempIndex);
			ContextPropagatingThreadFactory contextPropagatingThreadFactory = new ContextPropagatingThreadFactory("BlockChainByIndexReader");
			final List<Thread> threads = new ArrayList<>(tempIndex.size());
			tempIndex.forEach(i -> {
				Thread thread = contextPropagatingThreadFactory.newThread(new Runnable() {
					
					@Override
					public void run() {
						final Block readBlock = blockLoader.readBlock(i);
						if (readBlock != null) {
							log.info("Readed block: " + readBlock.getHashAsString());
							synchronized (blocks) {
								blocks.add(readBlock);
							}
						}
					}
				});
				threads.add(thread);
				thread.start();
			});
			Utils.waitForAllThreads(threads);
			log.info("Readed " + blocks.size() + " blocks with batching " + BLOCK_COUNT);
			final Blockchain blockchain = BlockchainHelper.recreateBlockchain(blocks, true, false);
			returnBlocks = blockchain.getBlocks();
		}
		return returnBlocks;
	}

	@Override
	public Block readBlock(String blockHash) {
		final BlockIndex readIndex = repository.readIndex(blockHash);
		Block block = null;
		if (readIndex != null) {
			block = blockLoader.readBlock(readIndex);
			log.info("Readed block: " + block.getHashAsString());
		}
		return block;
	}
}
