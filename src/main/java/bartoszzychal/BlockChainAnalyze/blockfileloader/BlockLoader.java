package bartoszzychal.BlockChainAnalyze.blockfileloader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bitcoinj.core.Block;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.core.ProtocolException;
import org.bitcoinj.core.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bartoszzychal.BlockChainAnalyze.persistance.BlockIndex;

public class BlockLoader {

	private NetworkParameters params;
	private static final Logger log = LoggerFactory.getLogger(BlockLoader.class);
	private final Map<String, File> filesMap;

    public BlockLoader(NetworkParameters params, List<File> files) {
		this.params = params;
    	final Map<String, File> filesMap = new HashMap<>();
    	for (File file : files) {
			filesMap.put(file.getName(), file);
		}
    	this.filesMap = filesMap;
    }

	public Block readBlock(BlockIndex index) {
		Block block = null;
		final String fileName = index.getFileName();
		//check if file exists
		if (filesMap.containsKey(fileName)) {
			final File file = filesMap.get(fileName);
			//open file
			try (final FileInputStream currentFileStream = new FileInputStream(file)) {
				//skip to start Block Byte from index
				final Long startFromByte = index.getStartFromByte();
				currentFileStream.skip(startFromByte);
				//if can read parse block 
				if (currentFileStream.available() > 0) {
					byte[] bytes = new byte[4];
					currentFileStream.read(bytes, 0, 4);
					long size = Utils.readUint32BE(Utils.reverseBytes(bytes), 0);
					// We allow larger than MAX_BLOCK_SIZE because test code uses this as well.
					if (size < Block.MAX_BLOCK_SIZE*2 && size >= 0) {
						bytes = new byte[(int) size];
						currentFileStream.read(bytes, 0, (int) size);
						try {
							block = params.getDefaultSerializer().makeBlock(bytes);
						} catch (ProtocolException e) {
							System.out.println(e);
						}
					}
				}
				// check if block was right indexed
				if (!block.getHashAsString().equals(index.getBlockHash())) {
					log.error("WRONG INDEXING OF BLOCK: " + index.getBlockHash());
					block = null;
				}
				
			} catch (FileNotFoundException e) {
				log.warn("Can not open file: " + fileName);
				e.printStackTrace();
			} catch (IOException e1) {
				log.warn("Can read from file: " + fileName);
				e1.printStackTrace();
			}
				
		}
		return block;
	}
}
