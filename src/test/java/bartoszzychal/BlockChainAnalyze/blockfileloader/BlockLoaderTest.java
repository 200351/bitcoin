package bartoszzychal.BlockChainAnalyze.blockfileloader;

import static org.junit.Assert.*;

import org.bitcoinj.core.Block;
import org.bitcoinj.core.Context;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.params.MainNetParams;
import org.junit.Test;

import bartoszzychal.BlockChainAnalyze.dbconnection.IBitCoinIndexRepository;
import bartoszzychal.BlockChainAnalyze.dbconnection.hsql.HsqlBitcoinIndexRepository;
import bartoszzychal.BlockChainAnalyze.fileloader.FileLoader;
import bartoszzychal.BlockChainAnalyze.index.persistance.BlockIndex;

public class BlockLoaderTest {

	final static NetworkParameters np = new MainNetParams();
	final static Context context = new Context(np);
	
	
	@Test
	public void test() {
		BlockLoader loader = new BlockLoader(np, FileLoader.readFiles());	
		final IBitCoinIndexRepository repository = new HsqlBitcoinIndexRepository();
		final BlockIndex readIndex = repository.readIndex("0000000000029af88c2052bf97cbb90a9d3307040a37f6260d124d076694f5c1");;
		final Block readBlock = loader.readBlock(readIndex);
		assertNotNull(readBlock);
	}

}
