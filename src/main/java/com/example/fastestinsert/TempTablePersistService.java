package com.example.fastestinsert;

import com.google.common.collect.Lists;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

@Service
@RequiredArgsConstructor
@Transactional
public class TempTablePersistService<T>  {

    private static final int MAX_BIND_VALUES = 50;
    private static final double BATCH_SIZE = 1000;

    @Value("${ic.db.temp-table-prefix:ttbl}")
    String globalTempTablePrefix;
    @PersistenceContext
    private final EntityManager entityManager;

    private final TempTableSqlService tempTableSqlService;

    private void doWithPopulatedTempTable(List<T> entities,
                                          TableDescriptor<T> descriptor,
                                          Consumer<TempTableDescriptor<T>> work, int maxBindValues, double batchSize) {
        TempTableDescriptor<T> tempTableDescriptor = getTempTableDescriptor(descriptor);
//        entityManager
//                .createNativeQuery(tempTableSqlService.getCreateTable(tempTableDescriptor))
//                .executeUpdate();

        Session session = entityManager.unwrap(Session.class);

        session.doWork(connection -> insertEntitiesInBatches(entities,
                tempTableDescriptor,
                connection,
                maxBindValues,
                batchSize));

//        work.accept(tempTableDescriptor);

//        entityManager
//                .createNativeQuery(tempTableSqlService.getDropTableSql(tempTableDescriptor))
//                .executeUpdate();
    }

    public void insertEntities(List<T> entities, TableDescriptor<T> descriptor, int maxBindValues, double batchSize) {
        doWithPopulatedTempTable(entities, descriptor, (tempTableDescriptor) -> {
                    String insertQuery = tempTableSqlService.getInsertIntoOriginalTable(tempTableDescriptor);
                    entityManager.createNativeQuery(insertQuery).executeUpdate();
                }, maxBindValues, batchSize
        );
    }

    public void updateEntities(List<T> entities, TableDescriptor<T> descriptor) {
        doWithPopulatedTempTable(entities, descriptor, (tempTableDescriptor) -> {
            String updateQuery = tempTableSqlService.getUpdateOriginalTable(tempTableDescriptor);
            entityManager.createNativeQuery(updateQuery).executeUpdate();
        }, MAX_BIND_VALUES, BATCH_SIZE);
    }

    private void insertEntitiesInBatches(List<T> entities,
                                         TempTableDescriptor<T> descriptor,
                                         Connection connection,
                                         int maxBindValues, double batchSize) throws SQLException {

        TableDescriptor<T> tableDescriptor = descriptor.getTableDescriptor();
        int valueBindingRepetitions = maxBindValues / tableDescriptor.getColumns().size();

        List<List<T>> chunks = getChunks(entities, valueBindingRepetitions);

        PreparedStatement preparedStatement = prepareStatement(descriptor, connection, valueBindingRepetitions);

        for (int i = 0; i < chunks.size(); i++) {
            List<T> chunk = chunks.get(i);
            if (chunk.size() < valueBindingRepetitions && i == chunks.size() - 1) {
                preparedStatement.executeBatch();
                preparedStatement.clearBatch();
                preparedStatement = prepareStatement(descriptor, connection, chunk.size());
                insertChunk(tableDescriptor, preparedStatement, chunk);
                break;
            }

            insertChunk(tableDescriptor, preparedStatement, chunk);
            if ((i + 1) % batchSize == 0) {
                preparedStatement.executeBatch();
                preparedStatement.clearBatch();
            }
        }
        preparedStatement.executeBatch();
        preparedStatement.clearBatch();
    }
    public static <T> List<List<T>> getChunks(List<T> originalList, int chunkSize) {
        if (CollectionUtils.isEmpty(originalList)) {
            return Collections.emptyList();
        }

        return Lists.partition(originalList, chunkSize);
    }


    private void insertChunk(TableDescriptor<T> tableDescriptor,
                             PreparedStatement preparedStatement,
                             List<T> chunk) throws SQLException {
        int paramIndex = 0;
        for (T entity : chunk) {
            for (int i = 0; i < tableDescriptor.getColumns().size(); i++) {
                ColumnDescriptor<T> columnDescriptor = tableDescriptor.getColumns().get(i);
                preparedStatement.setObject(++paramIndex, columnDescriptor.getEntityFieldValue(entity));
            }
        }
        preparedStatement.addBatch();
    }

    private PreparedStatement prepareStatement(TempTableDescriptor<T> descriptor,
                                               Connection connection,
                                               int valueBindingRepetitions) throws SQLException {
        String insertValuesQuery = tempTableSqlService.getInsertQuery(descriptor, valueBindingRepetitions);
        return connection.prepareStatement(insertValuesQuery);
    }


    private TempTableDescriptor<T> getTempTableDescriptor(TableDescriptor<T> descriptor) {
        return TempTableDescriptor.of(descriptor, generateTableName());
    }
    private String generateTableName() {
        return "##" +
                globalTempTablePrefix
                + '_'
                + UUID
                .randomUUID()
                .toString()
                .replace('-', '_');
    }

}
