package bartoszzychal.BlockChainAnalyze.model;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.bitcoinj.core.Block;

public class BlockchainRang {
	private LinkedList<Block> blocks;
	private List<Block> orphanedBlocks;
	
	
	
	public BlockchainRang() {
		this.blocks =  new LinkedList<>();
		this.orphanedBlocks = new ArrayList<>();
	}
	public LinkedList<Block> getBlocks() {
		return blocks;
	}
	public List<Block> getOrphanedBlock() {
		return orphanedBlocks;
	}
	
}
