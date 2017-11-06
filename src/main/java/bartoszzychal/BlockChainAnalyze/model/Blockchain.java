package bartoszzychal.BlockChainAnalyze.model;

import java.util.LinkedHashMap;
import java.util.List;

import org.apache.commons.collections4.MapUtils;
import org.bitcoinj.core.Block;
import org.bitcoinj.core.Sha256Hash;

public class Blockchain {
	private List<Block> blocks;
	private LinkedHashMap<Sha256Hash, Block> orphanedBlocks;
	private static Blockchain blockchain;
	
	private Blockchain() {
	}

	public List<Block> getBlocks() {
		return blocks;
	}
	public void setBlocks(List<Block> blocks) {
		this.blocks = blocks;
	}
	public LinkedHashMap<Sha256Hash, Block> getOrphanedBlocks() {
		return orphanedBlocks;
	}
	public void setOrphanedBlocks(LinkedHashMap<Sha256Hash, Block> orphanedBlocks) {
		this.orphanedBlocks = orphanedBlocks;
	}
	
	public void addOrphanedBlock(Block block) {
		if (MapUtils.isEmpty(orphanedBlocks)) {
			orphanedBlocks = new LinkedHashMap<>();
		}
		
		orphanedBlocks.put(block.getHash(), block);
	}

	public void removedOrphanedBlock(Block block) {
		if (MapUtils.isNotEmpty(orphanedBlocks)) {
			orphanedBlocks.remove(block.getHash());
		}
		
	}
	
	public static Blockchain getInstance() {
		if (blockchain == null) {
			blockchain = new Blockchain();
		}
		return blockchain;
	}
}
