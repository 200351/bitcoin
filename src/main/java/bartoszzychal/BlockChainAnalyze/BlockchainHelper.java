package bartoszzychal.BlockChainAnalyze;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bartoszzychal.BlockChainAnalyze.model.Blockchain;

public class BlockchainHelper {
	
    private static final Logger log = LoggerFactory.getLogger(BlockchainHelper.class);

	public static Blockchain recreateBlockchain(List<Block> blocksInput, boolean reverse) {
		final LinkedList<Block> blocks = new LinkedList<>();
		final Blockchain blockchain = Blockchain.getInstance();
		if (CollectionUtils.isNotEmpty(blocksInput)) {
			log.info("Start : recreateBlockchain with: " + blocksInput.size() + " blocks.");
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

			final Block startBlock = blocksInput.get(0);
			blocks.add(startBlock);
			
			while (previousExists || nextExists) {
				previousExists = false;
				nextExists = false;
				
				final Block first = blocks.getFirst();
				final Block last = blocks.getLast();

				final Sha256Hash firstPrevBlockHash = first.getPrevBlockHash();
				final Sha256Hash lastBlockHash = last.getHash();

				if (blocksMap.containsKey(firstPrevBlockHash)) {
					final Block block = blocksMap.get(firstPrevBlockHash);
					blocks.addFirst(block);
					previousExists = true;
				}

				if (prevBlocksMap.containsKey(lastBlockHash)) {
					final Block block = prevBlocksMap.get(lastBlockHash);
					blocks.addLast(block);
					nextExists = true;
				}

				final LinkedHashMap<Sha256Hash, Block> orphanedBlocks = blockchain.getOrphanedBlocks();
				
				if (MapUtils.isNotEmpty(orphanedBlocks) && nextExists == false && previousExists == false) {
					if (orphanedBlocks.containsKey(firstPrevBlockHash)) {
						final Block block = blocksMap.get(firstPrevBlockHash);
						blocks.addFirst(block);
						previousExists = true;
						log.info("Reused orphaned block: " + block.getHash());
						blockchain.removedOrphanedBlock(block);
					}
					
					if (orphanedBlocks.containsKey(lastBlockHash)) {
						final Block block = prevBlocksMap.get(lastBlockHash);
						blocks.addLast(block);
						nextExists = true;
						log.info("Reused orphaned block: " + block.getHash());
						blockchain.removedOrphanedBlock(block);
					}
				}

			}
			blockchain.setBlocks(reverse ? blocks.stream().collect(reversed()).collect(Collectors.toList()) : blocks);
			final Collection<Block> remainingBlocks = blocksMap.values();
			final Collection<Block> remainingPrevBlocks = prevBlocksMap.values();
			remainingBlocks.retainAll(remainingPrevBlocks);
			remainingBlocks.stream().forEach(b -> blockchain.addOrphanedBlock(b));
		}
		log.info("End : recreateBlockchain with: " + blocks.size() + " blocks.");
		return blockchain;
	}
	
	
	public static <T> Collector<T, ?, Stream<T>> reversed() {
	    return Collectors.collectingAndThen(Collectors.toList(), list -> {
	        Collections.reverse(list);
	        return list.stream();
	    });
	}
}
