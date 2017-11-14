package bartoszzychal.BlockChainAnalyze.dbconnection.hsql;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;

import javax.persistence.Query;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.bitcoinj.core.Block;

import bartoszzychal.BlockChainAnalyze.dbconnection.IBitCoinIndexRepository;
import bartoszzychal.BlockChainAnalyze.dbconnection.impl.EntityManagerProvider;
import bartoszzychal.BlockChainAnalyze.index.mapper.PersistanceMapper;
import bartoszzychal.BlockChainAnalyze.index.persistance.BlockIndex;
import bartoszzychal.BlockChainAnalyze.index.persistance.Transaction;
import bartoszzychal.BlockChainAnalyze.index.persistance.TransactionInput;
import bartoszzychal.BlockChainAnalyze.index.persistance.TransactionOutput;

public class HsqlBitcoinIndexRepository implements IBitCoinIndexRepository {

	private static final int IN_LIMIT = 1001;
	private final String ADDRESSES_HASH_PARAM = "blockHashParam";
	private final String BLOCK_HASH_PARAM = "blockHashParam";
	private final String BLOCK_START_PARAM = "blockStartParam";
	private final String BLOCK_END_PARAM = "blockEndParam";
	private static final Logger log = Logger.getLogger(HsqlBitcoinIndexRepository.class);

	public HsqlBitcoinIndexRepository() {
	}
	
	@Override
	public synchronized BlockIndex readIndex(String blockHash) {
		BlockIndex blockIndex = null;
		if (StringUtils.isNotBlank(blockHash)) {
			final Query query = EntityManagerProvider.getEntityManager()
					.createQuery("select bi from BlockIndex bi where bi.blockHash = :" + BLOCK_HASH_PARAM);
			query.setMaxResults(1);
			query.setParameter(BLOCK_HASH_PARAM, blockHash);
			EntityManagerProvider.beginTransaction();
			List<BlockIndex>blockIndexes = query.getResultList();
			if (CollectionUtils.isNotEmpty(blockIndexes)) {
				blockIndex = blockIndexes.get(0);
			}
			EntityManagerProvider.commit();
		}
		return blockIndex;
	}

	@Override
	public synchronized List<BlockIndex> readIndexedBlocks(LocalDate start, LocalDate end) {
		List<BlockIndex> blockIndexes = null;
		if (start != null && end != null) {
			LocalDateTime startDate = LocalDateTime.of(start, LocalTime.MIN);
			LocalDateTime endDate = LocalDateTime.of(end, LocalTime.MAX);
			final Query query = EntityManagerProvider.getEntityManager()
					.createQuery("select bi from BlockIndex bi"
							+ " where bi.generatedDate >= :" + BLOCK_START_PARAM
							+ " and bi.generatedDate <= :" + BLOCK_END_PARAM
							+ " order by bi.generatedDate desc");
			query.setParameter(BLOCK_START_PARAM, startDate);
			query.setParameter(BLOCK_END_PARAM, endDate);
			final List<BlockIndex> resultList = query.getResultList();
			
			EntityManagerProvider.beginTransaction();
			if (CollectionUtils.isNotEmpty(resultList)) {
				blockIndexes = resultList;
			}
			EntityManagerProvider.commit();
		}
		return blockIndexes;
	}

	@Override
	public BlockIndex createNewIndexForBlock(Block block, String fileName, Long startFromByte) {
		BlockIndex blockIndex = null;
		if (block != null && StringUtils.isNotBlank(block.getHashAsString())) {
			final String hashAsString = block.getHashAsString();
			if (readIndex(hashAsString) == null) {
				blockIndex = PersistanceMapper.map(block);
				blockIndex.setFileName(fileName);
				blockIndex.setStartFromByte(startFromByte);
				EntityManagerProvider.beginTransaction();
				EntityManagerProvider.getEntityManager().persist(blockIndex);
				EntityManagerProvider.commit();
			}
		}
		return blockIndex;
	}

	@Override
	public BlockIndex createNewIndexForBlock(BlockIndex blockIndex, boolean reindex) {
		if (blockIndex == null) {
			log.warn("Block Index == null. Skip");
		}
		if (StringUtils.isBlank(blockIndex.getBlockHash())) {
			log.warn("Block Hash Index == null. Skip");
		}
		if (blockIndex.getFileName() == null) {
			log.warn("File Name Index == null. Skip " + blockIndex.getBlockHash());
		}
		if (blockIndex.getStartFromByte() == null) {
			log.warn("Start From Byte Index == null. Skip " + blockIndex.getBlockHash());
		}
		if (blockIndex.getGeneratedDate() == null) {
			log.warn("Generated Date Index == null. Skip " + blockIndex.getBlockHash());
		}
		
		
		BlockIndex oldReadIndex = null;

		if (reindex && (oldReadIndex = readIndex(blockIndex.getBlockHash())) != null) {
			removeIndex(oldReadIndex);
			oldReadIndex = null;
		}
		
		if (oldReadIndex == null) {
			createIndex(blockIndex);
		}
		
		return blockIndex;
	}

	@Override
	public void removeIndex(BlockIndex index) {
		EntityManagerProvider.beginTransaction();
		EntityManagerProvider.getEntityManager().remove(index);
		EntityManagerProvider.commit();
	}
	
	@Override
	public void createIndex(BlockIndex index) {
		EntityManagerProvider.beginTransaction();
		EntityManagerProvider.getEntityManager().persist(index);
		EntityManagerProvider.commit();
	}

	@Override
	public List<BlockIndex> readIndexedBlocks(LocalDate start, LocalDate end, List<String> addresses) {
		List<BlockIndex> blockIndexes = null;
		if (start != null && end != null && CollectionUtils.isNotEmpty(addresses) && addresses.size() < IN_LIMIT) {
			LocalDateTime startDate = LocalDateTime.of(start, LocalTime.MIN);
			LocalDateTime endDate = LocalDateTime.of(end, LocalTime.MAX);
			final Query query = EntityManagerProvider.getEntityManager()
					.createQuery("select bi from BlockIndex bi"
							+ " inner join Transaction t"
							+ " inner join TransactionOutput to"
							+ " where bi.generatedDate >= :" + BLOCK_START_PARAM
							+ " and bi.generatedDate <= :" + BLOCK_END_PARAM
							+ " and to.address in (:" + ADDRESSES_HASH_PARAM + ")"
							+ " order by bi.generatedDate desc");
			query.setParameter(BLOCK_START_PARAM, startDate);
			query.setParameter(BLOCK_END_PARAM, endDate);
			query.setParameter(ADDRESSES_HASH_PARAM, addresses);
			final List<BlockIndex> resultList = query.getResultList();
			
			EntityManagerProvider.beginTransaction();
			if (CollectionUtils.isNotEmpty(resultList)) {
				blockIndexes = resultList;
			}
			EntityManagerProvider.commit();
		}
		return blockIndexes;
	}

	@Override
	public synchronized void createNewIndexForBlock(List<BlockIndex> blockIndex, boolean reindex) {
		EntityManagerProvider.beginTransaction();
		for (int i = 0; i < blockIndex.size(); i++) {
			BlockIndex index = blockIndex.get(i);
			
			BlockIndex oldReadIndex = null;

			if (reindex && (oldReadIndex = readIndex(index.getBlockHash())) != null) {
				EntityManagerProvider.getEntityManager().remove(oldReadIndex);
				EntityManagerProvider.getEntityManager().flush();
			}
			log.info(i + ".Persist block: " + index.getBlockHash());
			EntityManagerProvider.getEntityManager().persist(index);
			if (i % BATCH_SIZE == 0) {
				log.info(BATCH_SIZE + " Flush.");
				EntityManagerProvider.getEntityManager().flush();
				EntityManagerProvider.getEntityManager().clear();
			}
		}
		
		EntityManagerProvider.commit();
	}
	
}
	
