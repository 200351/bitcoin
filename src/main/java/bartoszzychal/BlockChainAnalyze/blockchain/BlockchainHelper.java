package bartoszzychal.BlockChainAnalyze.blockchain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.bitcoinj.core.Block;
import org.bitcoinj.core.Sha256Hash;

import org.apache.log4j.Logger;

import bartoszzychal.BlockChainAnalyze.model.Blockchain;
import bartoszzychal.BlockChainAnalyze.model.BlockchainRang;

public class BlockchainHelper {
	
    private static final Logger log = Logger.getLogger(BlockchainHelper.class);

	public static Blockchain recreateBlockchain(List<Block> blocksInput, boolean reverse, boolean saveOrphandedBlocks) {
		final Blockchain blockchain = Blockchain.getInstance();
		if (CollectionUtils.isNotEmpty(blocksInput)) {
			log.info("Start recreating Blockchain with " + blocksInput.size() + " blocks.");
			final LinkedHashMap<Sha256Hash, Block> blocksMap = new LinkedHashMap<>();
			final LinkedHashMap<Sha256Hash, Block> prevBlocksMap = new LinkedHashMap<>();
			for (Block block : blocksInput) {
				final Sha256Hash hash = block.getHash();
				if (hash != null) {
					blocksMap.put(hash, block);
				}
				final Sha256Hash prevBlockHash = block.getPrevBlockHash();
				if (prevBlockHash != null) {
					prevBlocksMap.put(prevBlockHash, block);
				}
			}

			boolean previousExists = true;
			boolean nextExists = true;

			final List<BlockchainRang> rangs = new ArrayList<>();
			for (Block block : blocksInput) {
				final BlockchainRang rang = new BlockchainRang();
				rang.getBlocks().add(block);
				while (previousExists || nextExists) {
					previousExists = false;
					nextExists = false;
					
					final Block first = rang.getBlocks().getFirst();
					final Block last =  rang.getBlocks().getLast();

					final Sha256Hash firstPrevBlockHash = first.getPrevBlockHash();
					final Sha256Hash lastBlockHash = last.getHash();

					if (blocksMap.containsKey(firstPrevBlockHash)) {
						final Block blockPrev = blocksMap.get(firstPrevBlockHash);
						rang.getBlocks().addFirst(blockPrev);
						previousExists = true;
					}

					if (prevBlocksMap.containsKey(lastBlockHash)) {
						final Block blockNext = prevBlocksMap.get(lastBlockHash);
						rang.getBlocks().addLast(blockNext);
						nextExists = true;
					}

					final LinkedHashMap<Sha256Hash, Block> orphanedBlocks = blockchain.getOrphanedBlocks();
					
					if (saveOrphandedBlocks && MapUtils.isNotEmpty(orphanedBlocks) && nextExists == false
							&& previousExists == false) {
						if (firstPrevBlockHash != null && orphanedBlocks.containsKey(firstPrevBlockHash)) {
							final Block blockPrev = blocksMap.get(firstPrevBlockHash);
							if (blockPrev != null) {
								rang.getBlocks().addFirst(blockPrev);
								previousExists = true;
								log.info("Reused orphaned block: " + blockPrev.getHash());
								rang.getOrphanedBlock().add(blockPrev);
								blockchain.removedOrphanedBlock(blockPrev);
							}
						}
						
						if (lastBlockHash != null && orphanedBlocks.containsKey(lastBlockHash)) {
							final Block blockNext = prevBlocksMap.get(lastBlockHash);
							if (blockNext != null) {
								rang.getBlocks().addLast(blockNext);
								nextExists = true;
								log.info("Reused orphaned block: " + blockNext.getHash());
								rang.getOrphanedBlock().add(blockNext);
							}
						}
					}
				}
				rangs.add(rang);
			}
			
			//The winner blockchain is
			final BlockchainRang blockchainRang = rangs.stream()
					.max((r1, r2) -> Long.valueOf(r1.getBlocks().size()).compareTo(Long.valueOf(r2.getBlocks().size())))
					.get();			
	
			
			// Get blocks from winner Blockchain
			final LinkedList<Block> blocks = blockchainRang.getBlocks();
			
			// set the Global Blockchain
			blockchain.setBlocks(reverse ? blocks.stream().collect(reversed()).collect(Collectors.toList()) : blocks);

			if (saveOrphandedBlocks) {
				blockchainRang.getOrphanedBlock().stream().forEach(b -> blockchain.removedOrphanedBlock(b));
				final Collection<Block> remainingBlocks = blocksMap.values();
				final Collection<Block> remainingPrevBlocks = prevBlocksMap.values();
				
				// add OrphanedBlocks
				// get Intersect of two Maps
				remainingBlocks.retainAll(remainingPrevBlocks);
				// add as Orphaned Blocks
				remainingBlocks.stream().forEach(b -> blockchain.addOrphanedBlock(b));
			}
			// Removed used Orphaned Blocks
			log.info("End recreating Blockchain with " + blocks.size() + " blocks.");
		}
		return blockchain;
	}
	
	
	public static <T> Collector<T, ?, Stream<T>> reversed() {
	    return Collectors.collectingAndThen(Collectors.toList(), list -> {
	        Collections.reverse(list);
	        return list.stream();
	    });
	}
}
