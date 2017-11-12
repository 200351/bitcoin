package bartoszzychal.BlockChainAnalyze.blockfileloader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import org.bitcoinj.core.Block;
import org.bitcoinj.core.Context;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.core.ProtocolException;
import org.bitcoinj.core.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.io.CountingInputStream;

import bartoszzychal.BlockChainAnalyze.index.IndexCreator;
import bartoszzychal.BlockChainAnalyze.persistance.BlockIndex;

public class BlockIndexLoader implements Iterable<BlockIndex>, Iterator<BlockIndex> {
   
	private Iterator<File> fileIt;
    private FileInputStream currentFileStream = null;
    private CountingInputStream countingInputStream = null;
    private BlockIndex nextBlockIndex = null;
    private NetworkParameters params;
    private File file;
	private Context context;
	private static final Logger log = LoggerFactory.getLogger(BlockIndexLoader.class);

    public BlockIndexLoader(NetworkParameters params, List<File> files) {
        fileIt = files.iterator();
        this.params = params;
        this.context = new Context(params);
    }
    
    @Override
    public boolean hasNext() {
        if (nextBlockIndex == null)
            loadNextBlock();
        return nextBlockIndex != null;
    }

    @Override
    public BlockIndex next() throws NoSuchElementException {
        if (!hasNext())
            throw new NoSuchElementException();
        BlockIndex next = nextBlockIndex;
        nextBlockIndex = null;
        return next;
    }
    
    private void loadNextBlock() {
        while (true) {
            try {
                if (!fileIt.hasNext() && (countingInputStream == null || countingInputStream.available() < 1))
                    break;
            } catch (IOException e) {
                currentFileStream = null;
                countingInputStream = null;
                file = null;
                if (!fileIt.hasNext())
                    break;
            }
            while (true) {
                try {
                    if (countingInputStream != null && countingInputStream.available() > 0)
                        break;
                } catch (IOException e1) {
                    currentFileStream = null;
                    countingInputStream = null;
                    file = null;
                }
                if (!fileIt.hasNext()) {
                	nextBlockIndex = null;
                    currentFileStream = null;
                    countingInputStream = null;
                    file = null;
                    return;
                }
                try {
                    File file = fileIt.next();
                    this.file = file;
                    log.info("Read file " + file.getName());
					currentFileStream = new FileInputStream(file);
                    countingInputStream = new CountingInputStream(currentFileStream);
                } catch (FileNotFoundException e) {
                    currentFileStream = null;
                    countingInputStream = null;
                    file = null;
                }
            }
            try {
                int nextChar = countingInputStream.read();
                while (nextChar != -1) {
                    if (nextChar != ((params.getPacketMagic() >>> 24) & 0xff)) {
                        nextChar = countingInputStream.read();
                        continue;
                    }
                    nextChar = countingInputStream.read();
                    if (nextChar != ((params.getPacketMagic() >>> 16) & 0xff))
                        continue;
                    nextChar = countingInputStream.read();
                    if (nextChar != ((params.getPacketMagic() >>> 8) & 0xff))
                        continue;
                    nextChar = countingInputStream.read();
                    if (nextChar == (params.getPacketMagic() & 0xff))
                        break;
                }
                byte[] bytes = new byte[4];
                final Long count = countingInputStream.getCount();
                countingInputStream.read(bytes, 0, 4);
                long size = Utils.readUint32BE(Utils.reverseBytes(bytes), 0);
                // We allow larger than MAX_BLOCK_SIZE because test code uses this as well.
                if (size > Block.MAX_BLOCK_SIZE*2 || size <= 0)
                    continue;
                bytes = new byte[(int) size];
                countingInputStream.read(bytes, 0, (int) size);
                try {
                    final Block block = params.getDefaultSerializer().makeBlock(bytes);
                    final BlockIndex blockIndex = new BlockIndex();
                    blockIndex.setBlockHash(block.getHashAsString());
                    blockIndex.setGeneratedDate(bartoszzychal.BlockChainAnalyze.utils.Utils.parse(block.getTimeSeconds()));
                    blockIndex.setFileName(file.getName());
                    blockIndex.setStartFromByte(count);
                    nextBlockIndex = blockIndex;
                } catch (ProtocolException e) {
                	nextBlockIndex = null;
                    continue;
                }
                break;
            } catch (IOException e) {
                currentFileStream = null;
                countingInputStream = null;
                file = null;
                continue;
            }
        }
    }

    @Override
    public void remove() throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Iterator<BlockIndex> iterator() {
        return this;
    }
}
