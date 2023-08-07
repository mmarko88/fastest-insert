package com.example.fastestinsert;

import com.google.common.collect.Lists;
import jakarta.transaction.Transactional;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

@Service
@Transactional
public class SpringTemplateInsert {
    protected static final String INSERT_SQL = String
            .format("insert into person (%s, %s, %s, %s, %s)"
                            + " values (?,?,?,?,?)",
                    PersonConstants.PERSON_ID,
                    PersonConstants.FIRST_NAME,
                    PersonConstants.LAST_NAME,
                    PersonConstants.USER_NAME,
                    PersonConstants.YEARS);
    private final JdbcTemplate jdbcTemplate;

    public SpringTemplateInsert(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void insertPeople(List<Person> people, int batchSize) {
        jdbcTemplate.batchUpdate(INSERT_SQL,
                people,
                batchSize,
                SpringTemplateInsert::prepareRow);
    }

    public void insertPeople2(List<Person> people, int batchSize) {
        List<List<Person>> partitions = Lists.partition(people, batchSize);
        for (List<Person> chunk : partitions) {
            jdbcTemplate.batchUpdate(INSERT_SQL,
                    new BatchPreparedStatementSetter() {
                        @Override
                        public void setValues(PreparedStatement ps, int i) throws SQLException {
                            Person p = chunk.get(i);
                            prepareRow(ps, p);
                        }

                        @Override
                        public int getBatchSize() {
                            return chunk.size();
                        }
                    }
            );
        }
    }

    private static void prepareRow(PreparedStatement ps, Person p) throws SQLException {
        ps.setInt(1, p.getPersonId());
        ps.setString(2, p.getFirstName());
        ps.setString(3, p.getLastName());
        ps.setString(4, p.getUserName());
        ps.setInt(5, p.getYears());
//        ps.setDate(6, new Date(p.getCreationDate().getTime()));
    }
}
