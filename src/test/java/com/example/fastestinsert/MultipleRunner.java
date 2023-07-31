package com.example.fastestinsert;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.function.Consumer;

@Service
@Slf4j
public class MultipleRunner {
    @Value("${perftest.reprtitions:5}")
    private int repetitions;

    @Autowired
    private FastInsertService fastInsertService;

    @Autowired
    private TestResultService testResultService;

    public void execJob(int bindParamCount,
                        String testName,
                        FastestInsertApplicationTests.TestParam param,
                        Consumer<Void> job) {
        long duration;
        for (int i = 0; i < repetitions; i++) {
            fastInsertService.cleanUpPeopleTable();
            long startTime = System.currentTimeMillis();
            job.accept(null);
            duration = System.currentTimeMillis() - startTime;
            TestResult tr = new TestResult(
                    null,
                    testName,
                    new Date(),
                    duration,
                    param.people().size(),
                    param.batchSize(),
                    bindParamCount,
                    i,
                    param.testStartDate()
            );
            testResultService.save(tr);
        }
    }


}
