package com.example.fastestinsert;

import com.google.common.collect.Lists;
import jakarta.persistence.EntityManager;
import jakarta.persistence.FlushModeType;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Repository
@Slf4j
@Transactional
public class ChunkEntityPersistRepositoryImpl implements ChunkEntityPersistRepository {
	@PersistenceContext
	private EntityManager entityManager;

	public ChunkEntityPersistRepositoryImpl() {
	}

	@Override
	public <T> void persistAndFlushInChunks(List<T> objects, int bulkInsertBatchSize) {
		if(CollectionUtils.isEmpty(objects)){
			return;
		}

		entityManager.setFlushMode(FlushModeType.COMMIT);
		flushAndClearEntityManagerChanges();
		for(List<T> chunk : Lists.partition(objects, bulkInsertBatchSize)) {
			persistAndFlushObjects(chunk);
		}
		entityManager.setFlushMode(FlushModeType.AUTO);
	}

	private <T> void persistAndFlushObjects(Iterable<T> objects) {
			persistIterable(objects);
			flushAndClearEntityManagerChanges();
	}

	private <T> void persistIterable(Iterable<T> objects) {
		for (T object : objects) {
			entityManager.persist(object);
		}
	}

	private void flushAndClearEntityManagerChanges() {
		entityManager.flush();
		entityManager.clear();
	}
}
