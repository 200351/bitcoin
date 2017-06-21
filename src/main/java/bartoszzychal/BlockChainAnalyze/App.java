package bartoszzychal.BlockChainAnalyze;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bitcoinj.core.Block;
import org.bitcoinj.core.Context;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.params.MainNetParams;
import org.bitcoinj.utils.BlockFileLoader;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
    	NetworkParameters np = new MainNetParams();
    	List<File> blockChainFiles = new ArrayList<File>();
    	blockChainFiles.add(new File("D:/PWR/mgr/Praca Magisterska/BitCoinCore/BitcoinCoreInstall/blocks/blk00000.dat"));
    	BlockFileLoader bfl = new BlockFileLoader(np, blockChainFiles);
    	Context context = new Context(np);
    	// Data structures to keep the statistics.
    	Map<String, Integer> monthlyTxCount = new HashMap<String, Integer>();
    	Map<String, Integer> monthlyBlockCount = new HashMap<String, Integer>();

    	// Iterate over the blocks in the dataset.
    	for (Block block : bfl) {

    	    // Extract the month keyword.
    	    String month = new SimpleDateFormat("yyyy-MM-dd").format(block.getTime());

    	    // Make sure there exists an entry for the extracted month.
    	    if (!monthlyBlockCount.containsKey(month)) {
    	        monthlyBlockCount.put(month, 0);
    	        monthlyTxCount.put(month, 0);
    	    }

    	    // Update the statistics.
    	    monthlyBlockCount.put(month, 1 + monthlyBlockCount.get(month));
    	    monthlyTxCount.put(month, block.getTransactions().size() + monthlyTxCount.get(month));

    	}

    	// Compute the average number of transactions per block per month.
    	Map<String, Float> monthlyAvgTxCountPerBlock = new HashMap<String, Float>();
    	for (String month : monthlyBlockCount.keySet())
    	    monthlyAvgTxCountPerBlock.put(
    	            month, (float) monthlyTxCount.get(month) / monthlyBlockCount.get(month));
    }
}
