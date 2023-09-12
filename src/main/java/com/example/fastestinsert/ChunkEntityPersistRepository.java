package com.example.fastestinsert;

import java.util.List;

public interface ChunkEntityPersistRepository {
	/**
	 * This method uses entity manager for client databases. It doesn't
	 * support mup, lprsglobal, sysecurity or other database.
	 * It is important to note that it flushes data to the database and that
	 * it clears the entity manager context.
	 * Use it only if you need to insert huge amount of data and just for insert!!!
	 *
	 * @param objects list of new objects that needs to be inserted into db
	 */
	<T> void persistAndFlushInChunks(List<T> objects, int bulkInsertBatchSize);
}
