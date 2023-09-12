package com.example.fastestinsert;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

@Repository
@Slf4j
@Transactional
public class BulkPersistCustomRepositoryImpl implements BulkPersistCustomRepository {
    @PersistenceContext
    private final EntityManager entityManager;

    public BulkPersistCustomRepositoryImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public <T> void insert(List<T> objects, TableDescriptor<T> descriptor, int batchSize, int objectsPerInsert) {
        Session session = entityManager.unwrap(Session.class);
        session.doWork(connection ->
                insertInternal(objects, descriptor, connection, batchSize, objectsPerInsert));
    }

    private <T> void insertInternal(List<T> objects,
                                    TableDescriptor<T> descriptor,
                                    Connection connection,
                                    int batchSize,
                                    int objectsPerInsert) throws SQLException {

        // one chunk = one insert statement
        // for example insert into (...) values(???),(???)....
        // the whole chunk will be persisted in one insert statement
        List<List<T>> allChunks = Lists.partition(objects, objectsPerInsert);

        try (PreparedStatement preparedStatement =
                     prepareInsertStatement(descriptor, connection, objectsPerInsert)) {

            boolean persistData = false;
            for (int i = 0; i < allChunks.size(); i++) {
                List<T> currentChunk = allChunks.get(i);
                boolean isRegularChunkSize = currentChunk.size() == objectsPerInsert;
                if (isRegularChunkSize) {
                    insertBatchData(descriptor, preparedStatement, currentChunk);
                    preparedStatement.addBatch();
                    persistData = true;
                    if ((i + 1) % batchSize == 0) {
                        persistData = persistAndClearBatch(preparedStatement, true);
                    }
                } else {
                    persistData = persistAndClearBatch(preparedStatement, persistData);
                    insertBatchDataWithCustomSize(connection, descriptor, currentChunk);
                }
            }
            persistAndClearBatch(preparedStatement, persistData);
        }
    }

    private <T> void insertBatchDataWithCustomSize(Connection connection,
                                                   TableDescriptor<T> descriptor,
                                                   List<T> currentChunk) throws SQLException {
        try (PreparedStatement psCustomChunkSize = prepareInsertStatement(descriptor, connection, currentChunk.size())) {
            insertBatchData(descriptor, psCustomChunkSize, currentChunk);
            psCustomChunkSize.addBatch();
            persistAndClearBatch(psCustomChunkSize, true);
        }
    }

    private static boolean persistAndClearBatch(PreparedStatement preparedStatement, boolean persistData) throws SQLException {
        if (persistData) {
            preparedStatement.executeBatch();
            preparedStatement.clearBatch();
        }
        return false;
    }

    public static <T> void insertBatchData(TableDescriptor<T> tableDescriptor,
                                     PreparedStatement preparedStatement,
                                     List<T> objectsToInsert) throws SQLException {
        int paramIndex = 0;
        List<ColumnDescriptor<T>> columns = tableDescriptor.getColumns();
        for (T objectToInsert : objectsToInsert) {
            for (ColumnDescriptor<T> columnDescriptor : columns) {
                preparedStatement.setObject(++paramIndex, columnDescriptor.getObjectFieldValue(objectToInsert));
            }
        }
    }

    private PreparedStatement prepareInsertStatement(TableDescriptor<?> descriptor,
                                                     Connection connection,
                                                     int valueBindingRepetitions) throws SQLException {
        String insertValuesQuery = getInsertQuery(descriptor, valueBindingRepetitions);
        return connection.prepareStatement(insertValuesQuery);
    }

    public static String getInsertQuery(@NonNull TableDescriptor<?> descriptor, int numOfInsertsInStatement) {
        Preconditions.checkArgument(numOfInsertsInStatement > 0, "numOfInsertStatements must be > 0");
        return "INSERT INTO " +
                descriptor.getTableName() +
                " (" +
                descriptor.getColumnsCommaSeparated() +
                ") " +
                "VALUES " +
                repeatCommaSeparated(numOfInsertsInStatement, "(" +
                        repeatCommaSeparated(descriptor.getColumns().size(), "?") + ")");
    }

    private static String repeatCommaSeparated(int count, String string) {
        String values = Strings.repeat(string + ",", count);
        return values.substring(0, values.length() - 1);
    }

}
