package bartoszzychal.BlockChainAnalyze.dbconnection.hsql;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.TimeZone;

import javax.persistence.Query;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.bitcoinj.core.Block;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bartoszzychal.BlockChainAnalyze.dbconnection.IBitCoinIndexRepository;
import bartoszzychal.BlockChainAnalyze.index.IndexCreator;
import bartoszzychal.BlockChainAnalyze.persistance.BlockIndex;

public class HsqlBitcoinIndexRepository implements IBitCoinIndexRepository {

	private final String BLOCK_HASH_PARAM = "blockHashParam";
	private final String BLOCK_START_PARAM = "blockStartParam";
	private final String BLOCK_END_PARAM = "blockEndParam";
	private static final Logger log = LoggerFactory.getLogger(HsqlBitcoinIndexRepository.class);

	@Override
	public BlockIndex readIndex(String blockHash) {
		BlockIndex blockIndex = null;
		if (StringUtils.isNotBlank(blockHash)) {
			final Query query = getEntityManager()
					.createQuery("select bi from BlockIndex bi where bi.blockHash = :" + BLOCK_HASH_PARAM);
			query.setMaxResults(1);
			query.setParameter(BLOCK_HASH_PARAM, blockHash);
			openTransaction();
			List<BlockIndex>blockIndexes = query.getResultList();
			if (CollectionUtils.isNotEmpty(blockIndexes)) {
				blockIndex = blockIndexes.get(0);
			}
			closeTransaction();
		}
		return blockIndex;
	}

	@Override
	public List<BlockIndex> readIndexedBlocks(LocalDate start, LocalDate end) {
		List<BlockIndex> blockIndexes = null;
		if (start != null && end != null) {
			LocalDateTime startDate = LocalDateTime.of(start, LocalTime.MIN);
			LocalDateTime endDate = LocalDateTime.of(end, LocalTime.MAX);
			openTransaction();
			final Query query = getEntityManager()
					.createQuery("select bi from BlockIndex bi"
							+ " where bi.generatedDate >= :" + BLOCK_START_PARAM
							+ " and bi.generatedDate <= :" + BLOCK_END_PARAM);
			query.setParameter(BLOCK_START_PARAM, startDate);
			query.setParameter(BLOCK_END_PARAM, endDate);
			final List<BlockIndex> resultList = query.getResultList();
			
			if (CollectionUtils.isNotEmpty(resultList)) {
				blockIndexes = resultList;
			}
			closeTransaction();
		}
		return blockIndexes;
	}

	@Override
	public BlockIndex createNewIndexForBlock(Block block, String fileName, Long startFromByte) {
		BlockIndex blockIndex = null;
		if (block != null && StringUtils.isNotBlank(block.getHashAsString())) {
			final String hashAsString = block.getHashAsString();
			if (readIndex(hashAsString) == null) {
				BlockIndex newBlockIndex = new BlockIndex();
				newBlockIndex.setBlockHash(hashAsString);
				newBlockIndex.setFileName(fileName);
				newBlockIndex.setStartFromByte(startFromByte);
				newBlockIndex.setGeneratedDate(parse(block.getTimeSeconds()));
				getEntityManager().persist(newBlockIndex);
				blockIndex = newBlockIndex;
			}
		}
		return blockIndex;
	}

	private LocalDateTime parse(long time) {
		return LocalDateTime.ofInstant(Instant.ofEpochMilli(time), 
                TimeZone.getDefault().toZoneId());  
	}

	@Override
	public BlockIndex createNewIndexForBlock(BlockIndex blockIndex) {
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
		
		if (readIndex(blockIndex.getBlockHash()) == null) {
			getEntityManager().persist(blockIndex);
		}
		
		return blockIndex;
	}
}
	
