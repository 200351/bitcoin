package bartoszzychal.BlockChainAnalyze;

import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.Set;

import javax.print.attribute.standard.OutputDeviceAssigned;

import org.bitcoinj.core.Block;
import org.bitcoinj.core.Coin;
import org.bitcoinj.core.Sha256Hash;
import org.bitcoinj.core.Transaction;
import org.bitcoinj.params.TestNet3Params;
import org.junit.Test;

import bartoszzychal.BlockChainAnalyze.model.AbstractInfo;
import bartoszzychal.BlockChainAnalyze.model.InputInfo;
import bartoszzychal.BlockChainAnalyze.model.OutputInfo;
import bartoszzychal.BlockChainAnalyze.model.TransactionConnection;
import bartoszzychal.BlockChainAnalyze.model.TransactionSearchInfo;

public class TransactionCheckerTest {

	@Test
	public void test() {

		final InputInfo inputInfo = new InputInfo(Coin.FIFTY_COINS, "ADRESS");
		Set<AbstractInfo> info = new HashSet<>();
		info.add(inputInfo);
		
		TransactionSearchInfo searchInfo = new TransactionSearchInfo();
		searchInfo.setBlockHash(Sha256Hash.wrap("000000000000000004a89bd78bbaa336afb8ca11ef1e06af4f4617783a9dce12"));
		searchInfo.setTransactionHash(Sha256Hash.wrap("82e8621ba492cdd8101a78f7ecb886bb37cb5d577e4022db91cc9a6f81b9c5c6"));
		searchInfo.setInfo(info );
		
		Transaction transaction = new Transaction(TestNet3Params.get());
//		transaction.addOutput(new TransactionOutp);
		
//		final TransactionConnection areTransactionsConnected = TransactionChecker.areTransactionsConnected(searchInfo, transaction, block);
		
//		assertTrue(areTransactionsConnected.isConnected());
	}
	
	@Test
	public void test2() {
		InputInfo inputInfo = new InputInfo(Coin.CENT, "Address");
		OutputInfo outputInfo = new OutputInfo(Coin.ZERO, "Address");
		
		assertTrue(inputInfo.equals(outputInfo));
		assertTrue(inputInfo.hashCode() == outputInfo.hashCode());
		
		Set<InputInfo> inputInfos = new HashSet<>();
		Set<OutputInfo> outputInfos = new HashSet<>();
		
		inputInfos.add(inputInfo);
		outputInfos.add(outputInfo);
				
		assertTrue(inputInfos.retainAll(outputInfos));
	}

}
