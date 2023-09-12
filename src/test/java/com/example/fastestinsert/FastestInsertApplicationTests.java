package com.example.fastestinsert;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Stream;
@SpringBootTest(properties = "spring.config.location=classpath:application.properties")
@SpringJUnitConfig
@Slf4j
class FastestInsertApplicationTests {
    protected static final String NO_OF_PEOPLE_TO_CREATE = "noOfPeopleToCreate";
    @Autowired
    private PersonRepository personRepository;
    @Autowired
    private FastInsertService fastInsertService;
    @Autowired
    private MultipleRunner multipleRunner;

    @ParameterizedTest
    @MethodSource(NO_OF_PEOPLE_TO_CREATE)
    void regularBatchInsert(TestParam param) {
        multipleRunner.execJob("regularBatchInsert", param,
                (t) -> fastInsertService.insertPeopleBulkInsert(
                        param.getBatchSize(),
                        param.getObjectsPerInsert(),
                        param.getPeople()));
        assertTest(param);
    }

    @ParameterizedTest
    @MethodSource(NO_OF_PEOPLE_TO_CREATE)
    void jdbcTemplateInsert(TestParam param) {
        multipleRunner.execJob("jdbcTemplateInsert", param,
                (t) -> fastInsertService.insertPeopleSpringTemplateBulkInsert(param.getBatchSize(),
                        param.getObjectsPerInsert(),
                        param.getPeople()));
        assertTest(param);
    }

    @ParameterizedTest
    @MethodSource(NO_OF_PEOPLE_TO_CREATE)
    void bulkApiInsert(TestParam param) {
        if (param.getObjectsPerInsert() > 1) return;
        multipleRunner.execJob("bulkApiInsert", param,
                (t) -> fastInsertService.persistPeopleBulkApi(param.getPeople(), param.getBatchSize()));
        assertTest(param);
    }

    @ParameterizedTest
    @MethodSource(NO_OF_PEOPLE_TO_CREATE)
    void hibernatePersist(TestParam param) {
        if (param.getObjectsPerInsert() > 1) return;
        multipleRunner.execJob( "hibernatePersist", param,
                (t) -> fastInsertService.persistPeopleHibernateFlush(param.getPeople(), param.getBatchSize()));
        assertTest(param);
    }

    private void assertTest(TestParam param) {
        long count = personRepository.count();
        Assertions.assertThat(count).isEqualTo(param.getPeople().size());
    }
    public static Stream<TestParam> noOfPeopleToCreate() {
        List<TestParam> params = new ArrayList<>();
        Date startDate = new Date();
        int[] noOfPeople = {1, 10, 100, 400};
        int[] batchSizes = {10, 100, 1_000, 10_000};

//        int[] noOfPeople = {1};
//        int[] batchSizes = {1};

        for (int noOfPerson : noOfPeople) {
            List<Person> people = RandomPersonGenerator.generateRandomPersons(100_000);
            for (int batchSize : batchSizes) {
                params.add(new TestParam(people, batchSize, startDate, noOfPerson));
            }
        }
        return params.stream();
    }
}
