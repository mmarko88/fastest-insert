package com.example.fastestinsert;

import com.google.common.collect.Lists;
import jakarta.transaction.Transactional;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
@Transactional
public class SpringTemplateBulkInsertRepository {
   private final JdbcTemplate jdbcTemplate;

    public SpringTemplateBulkInsertRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void insertPeople(TableDescriptor<Person> tableDescriptor,
                             List<Person> people,
                             int batchSize,
                             int objectsPerInsert) {
        if (people.isEmpty()) return;
        objectsPerInsert = Math.min(objectsPerInsert, people.size());

        List<List<Person>> partitions = Lists.partition(people, objectsPerInsert);
        if (partitions.size() == 1 || partitions.get(partitions.size() -1).size() == objectsPerInsert) {
            persist(tableDescriptor, batchSize, objectsPerInsert, partitions);
        } else {
            List<List<Person>> equalSizePartitions = partitions.subList(0, partitions.size() - 1);
            persist(tableDescriptor, batchSize, objectsPerInsert, equalSizePartitions);
            List<List<Person>> lastPartition = Collections.singletonList(partitions.get(partitions.size() - 1));
            persist(tableDescriptor, 1, lastPartition.size(), lastPartition);
        }
    }

    private void persist(TableDescriptor<Person> tableDescriptor, int batchSize, int objectsPerInsert, List<List<Person>> partitions) {
        String insertQuery = BulkPersistCustomRepositoryImpl
                .getInsertQuery(tableDescriptor, objectsPerInsert);
        jdbcTemplate.batchUpdate(insertQuery,
                partitions,
                batchSize,
                (ps, argument) -> BulkPersistCustomRepositoryImpl.insertBatchData(tableDescriptor, ps, argument));
    }
}
