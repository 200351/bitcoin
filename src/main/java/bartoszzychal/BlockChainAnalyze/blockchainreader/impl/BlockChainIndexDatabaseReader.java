package bartoszzychal.BlockChainAnalyze.blockchainreader.impl;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.bitcoinj.core.Block;
import org.bitcoinj.core.Context;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.params.MainNetParams;

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
	public BlockChainIndexDatabaseReader(IBitCoinIndexRepository repository) {
		this.repository = repository;
		this.blockLoader = new BlockLoader(np, FileLoader.readFiles());
	}

	@Override
	public List<Block> readBlockChainFromTo(LocalDate from, LocalDate to) {
		List<Block> returnBlocks = null;
		if (from != null && to != null && to.isAfter(from)) {
			final List<BlockIndex> index = repository.readIndexedBlocks(from, to);
			final List<Block> blocks = new ArrayList<>();
			for (BlockIndex blockIndex : index) {
				final Block readBlock = blockLoader.readBlock(blockIndex);
				if (readBlock != null) {
					blocks.add(readBlock);
				}
			}
			final Blockchain blockchain = BlockchainHelper.recreateBlockchain(blocks, true, false);
			returnBlocks = blockchain.getBlocks();
		}
		return returnBlocks;
	}

}
