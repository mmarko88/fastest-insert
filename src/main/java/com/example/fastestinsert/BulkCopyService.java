package com.example.fastestinsert;

import com.microsoft.sqlserver.jdbc.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import javax.sql.RowSetMetaData;
import javax.sql.rowset.CachedRowSet;
import javax.sql.rowset.RowSetFactory;
import javax.sql.rowset.RowSetMetaDataImpl;
import javax.sql.rowset.RowSetProvider;
import java.sql.SQLException;
import java.util.List;

@Service
@Transactional
public class BulkCopyService {

    @Autowired
    private DataSource dataSource;

    @PersistenceContext
    EntityManager entityManager;

    public void performBulkInsert(List<Person> persons,
                                  TableDescriptor<Person> descriptor,
                                  int batchSize) throws SQLException {
        Session unwrap = entityManager.unwrap(Session.class);

        unwrap.doWork((connection) -> {
            try (SQLServerBulkCopy bulkCopy =
                         new SQLServerBulkCopy(connection.unwrap(SQLServerConnection.class))) {
                SQLServerBulkCopyOptions options = new SQLServerBulkCopyOptions();
                options.setBatchSize(batchSize);
                bulkCopy.setBulkCopyOptions(options);
                bulkCopy.setDestinationTableName(descriptor.getTableName());

                // Convert the List<Person> to SqlRowSet
                CachedRowSet dataToInsert = createCachedRowSet(persons, descriptor);
                // Perform bulk insert
                bulkCopy.writeToServer(dataToInsert);
            }
        });
    }

private CachedRowSet createCachedRowSet(List<Person> persons, TableDescriptor<Person> descriptor) throws SQLException {
        RowSetFactory factory = RowSetProvider.newFactory();
        CachedRowSet rowSet = factory.createCachedRowSet();

        rowSet.setMetaData(createMetadata(descriptor));

        for (Person person : persons) {
            rowSet.moveToInsertRow();
            for (int i = 0; i < descriptor.getColumns().size(); i++) {
                ColumnDescriptor<Person> column = descriptor.getColumns().get(i);
                rowSet.updateObject(i + 1, column.getEntityFieldValue(person), column.getSqlType());
            }
            rowSet.insertRow();
        }

        rowSet.moveToCurrentRow();
        return rowSet;
    }
    private static RowSetMetaData createMetadata(TableDescriptor<Person> descriptor) throws SQLException {
        RowSetMetaData metadata = new RowSetMetaDataImpl();

        // Set the number of columns
        metadata.setColumnCount(descriptor.getColumns().size());
        for (int i = 0; i < descriptor.getColumns().size(); i++) {
            metadata.setColumnName(i + 1, descriptor.getColumns().get(i).getColumnName());
            metadata.setColumnType(i + 1, descriptor.getColumns().get(i).getSqlType());
        }

        return metadata;
    }
}
