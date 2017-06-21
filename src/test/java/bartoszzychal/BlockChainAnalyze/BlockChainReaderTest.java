package bartoszzychal.BlockChainAnalyze;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.List;

import org.bitcoinj.core.Block;
import org.junit.Test;

public class BlockChainReaderTest {

	
	
	@Test
	public void testReadBlockChainFromTo() {
		// first block 2009-01-03
		LocalDate from = LocalDate.of(2009, 01, 03);
		LocalDate to = from.plusDays(20);
		
		BlockChainReader blockChainReader = BlockChainReader.getInstance();
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		
		while (to.isBefore(LocalDate.now())) {
//		while (to.isBefore(LocalDate.of(2010, 01, 01))) {
			List<Block> blocks = blockChainReader.readBlockChainFromTo(from, to);
			blocks.stream().forEach(b -> System.out.println(format.format(b.getTime())));
			from = from.plusDays(1);
			to = to.plusDays(1);
		}

	}

}
