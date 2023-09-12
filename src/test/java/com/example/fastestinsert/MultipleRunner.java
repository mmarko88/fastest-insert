package com.example.fastestinsert;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.function.Consumer;

@Service
@Slf4j
public class MultipleRunner {
    private static final int repetitions = 10;

    @Autowired
    private FastInsertService fastInsertService;

    @Autowired
    private TestResultService testResultService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public void execJob(String testName,
                        TestParam param,
                        Consumer<Void> job) {
        long duration;
        for (int i = 0; i < repetitions; i++) {
            fastInsertService.cleanUpPeopleTable();
            jdbcTemplate.execute("DBCC SHRINKFILE ('testdb_log', 1024)");

            long startTime = System.currentTimeMillis();
            job.accept(null);
            duration = System.currentTimeMillis() - startTime;
            TestResult tr = new TestResult(
                    null,
                    testName,
                    new Date(),
                    duration,
                    param.getPeople().size(),
                    param.getBatchSize(),
                    param.getObjectsPerInsert(),
                    i,
                    param.getTestStartDate()
            );
            testResultService.save(tr);
        }
    }


}
