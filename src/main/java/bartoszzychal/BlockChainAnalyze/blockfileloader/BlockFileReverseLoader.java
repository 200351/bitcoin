package bartoszzychal.BlockChainAnalyze.blockfileloader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;

import org.bitcoinj.core.Block;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.core.ProtocolException;
import org.bitcoinj.core.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bartoszzychal.BlockChainAnalyze.BlockchainHelper;
import bartoszzychal.BlockChainAnalyze.blockchainreader.impl.BlockChainReverseReader;

/**
 * <p>This class reads block files stored in the Bitcoin Core format. This is simply a way to concatenate
 * blocks together. Importing block data with this tool can be a lot faster than syncing over the network, if you
 * have the files available.</p>
 * 
 * <p>In order to comply with Iterator&lt;Block>, this class swallows a lot of IOExceptions, which may result in a few
 * blocks being missed followed by a huge set of orphan blocks.</p>
 * 
 * <p>To blindly import all files which can be found in Bitcoin Core (version >= 0.8) datadir automatically,
 * try this code fragment:<br>
 * BlockFileLoader loader = new BlockFileLoader(BlockFileLoader.getReferenceClientBlockFileList());<br>
 * for (Block block : loader) {<br>
 * &nbsp;&nbsp;try { chain.add(block); } catch (Exception e) { }<br>
 * }</p>
 */
public class BlockFileReverseLoader implements Iterable<Block>, Iterator<Block> {
    
	private Iterator<Block> blocks = null;
    private Iterator<File> fileIt;
    private FileInputStream currentFileStream = null;
    private Block nextBlock = null;
    private NetworkParameters params;
    
	private static final Logger log = LoggerFactory.getLogger(BlockFileReverseLoader.class);

    public BlockFileReverseLoader(NetworkParameters params, List<File> files) {
        fileIt = files.stream().sorted(Collections.reverseOrder()).iterator();
        this.params = params;
    }
    
    @Override
    public boolean hasNext() {
        if (nextBlock == null)
            loadNextBlock();
        return nextBlock != null;
    }

    @Override
    public Block next() throws NoSuchElementException {
        if (!hasNext())
            throw new NoSuchElementException();
        Block next = nextBlock;
        nextBlock = null;
        return next;
    }
    
    private void loadNextBlock() {
    	if (needLoadNextFile()) {
    		loadNextFile();
    		loadBlocks();
    	}
    	
    	if (blocks != null && blocks.hasNext()) {
    		nextBlock = blocks.next();
    		blocks.remove();
    	}
    }
    
    private void loadNextFile() {
		nextBlock = null;
		blocks = null;
		currentFileStream = null;
		if (fileIt != null  && fileIt.hasNext()) {
			 try {
                 currentFileStream = new FileInputStream(fileIt.next());
             } catch (FileNotFoundException e) {
                 currentFileStream = null;
             }
		}
		
	}

	private boolean needLoadNextFile() {
		return blocks == null || !blocks.hasNext();
	}

	private void loadBlocks() {
		if (currentFileStream != null) {
			try {
				final List<Block> blocks = BlockchainHelper.recreateBlockchain(prepareBlocks(), true);
				this.blocks = blocks.iterator();
			} catch (IOException e) {
				System.out.println(e);
			}
		}
    }

	private LinkedList<Block> prepareBlocks() throws IOException {
		final LinkedList<Block> blocks = new LinkedList<>();
		while (currentFileStream.available() > 0) {
			try {
				int nextChar = currentFileStream.read();
				while (nextChar != -1) {
					if (nextChar != ((params.getPacketMagic() >>> 24) & 0xff)) {
						nextChar = currentFileStream.read();
						continue;
					}
					nextChar = currentFileStream.read();
					if (nextChar != ((params.getPacketMagic() >>> 16) & 0xff))
						continue;
					nextChar = currentFileStream.read();
					if (nextChar != ((params.getPacketMagic() >>> 8) & 0xff))
						continue;
					nextChar = currentFileStream.read();
					if (nextChar == (params.getPacketMagic() & 0xff))
						break;
				}
				byte[] bytes = new byte[4];
				currentFileStream.read(bytes, 0, 4);
				long size = Utils.readUint32BE(Utils.reverseBytes(bytes), 0);
				// We allow larger than MAX_BLOCK_SIZE because test code uses this as well.
				if (size > Block.MAX_BLOCK_SIZE*2 || size <= 0)
					continue;
				bytes = new byte[(int) size];
				currentFileStream.read(bytes, 0, (int) size);
				try {
					final Block block = params.getDefaultSerializer().makeBlock(bytes);
					if (block != null) {
						blocks.add(block);
					}
				} catch (ProtocolException e) {
					System.out.println(e);
					continue;
				}
			} catch (IOException e) {
				System.out.println(e);
				continue;
			}
		}
		log.info("Read " + blocks.size() + " blocks.");
		return blocks;
	}

    @Override
    public void remove() throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Iterator<Block> iterator() {
        return this;
    }
}
