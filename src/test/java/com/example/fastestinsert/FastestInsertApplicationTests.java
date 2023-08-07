package com.example.fastestinsert;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Stream;

import static com.example.fastestinsert.PersonPersistService.PERSON_TABLE_DESCRIPTOR;

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
    @Autowired
    private BulkCopyService bulkCopyService;
    @Autowired
    private SpringTemplateInsert springTemplateInsert;

    @ParameterizedTest
    @MethodSource(NO_OF_PEOPLE_TO_CREATE)
    void bulkCopyInsert(TestParam param) {
        multipleRunner.execJob(0, "bulkCopyInsert", param,
                (t) -> {
                    try {
                        bulkCopyService.performBulkInsert(param.people(),
                                PERSON_TABLE_DESCRIPTOR,
                                param.batchSize());
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                });
        assertTest(param);
    }
    @ParameterizedTest
    @MethodSource(NO_OF_PEOPLE_TO_CREATE)
    void jdbcTemplateInsert(TestParam param) {
        multipleRunner.execJob(0, "jdbcTemplateInsert", param,
                (t) -> {
                    springTemplateInsert.insertPeople(param.people(),
                            param.batchSize());
                });
        assertTest(param);
    }

//    @ParameterizedTest
//    @MethodSource(NO_OF_PEOPLE_TO_CREATE)
//    void insertPeoplePersist(TestParam param) {
//        multipleRunner.execJob(0, "hibernateNoChunks", param,
//                (t) -> fastInsertService.insertPeoplePersist(param.people()));
//        assertTest(param);
//    }

//    @ParameterizedTest
//    @MethodSource(NO_OF_PEOPLE_TO_CREATE)
//    void insertPeoplePersistInChunks(TestParam param) {
//        multipleRunner.execJob(0, "HibernateChunks", param,
//                (t) -> fastInsertService
//                        .insertPeoplePersistInChunks(param.batchSize(), param.people()));
//        assertTest(param);
//    }

//    @ParameterizedTest
//    @MethodSource(NO_OF_PEOPLE_TO_CREATE)
//    void insertPeoplePersistPersistService10BindValues(TestParam param) {
//        multipleRunner.execJob(10, "PS_10_BindValues", param,
//                (t) -> fastInsertService
//                        .insertPeoplePersistPersistService(
//                                param.batchSize(),
//                                10,
//                                param.people()));
//        assertTest(param);
//    }


//    @ParameterizedTest
//    @MethodSource(NO_OF_PEOPLE_TO_CREATE)
//    void insertPeoplePersistPersistService100BindValues(TestParam param) {
//        multipleRunner.execJob(100,"PS_100_BindValues", param,
//                (t) -> fastInsertService
//                        .insertPeoplePersistPersistService(
//                                param.batchSize(),
//                                100,
//                                param.people()));
//        assertTest(param);
//    }
//
//    @ParameterizedTest
//    @MethodSource(NO_OF_PEOPLE_TO_CREATE)
//    void insertPeoplePersistPersistService500BindValues(TestParam param) {
//        multipleRunner.execJob(500,"PS_500_BindValues", param,
//                (t) -> fastInsertService
//                        .insertPeoplePersistPersistService(
//                                param.batchSize(),
//                                500,
//                                param.people()));
//        assertTest(param);
//    }
//
//    @ParameterizedTest
//    @MethodSource(NO_OF_PEOPLE_TO_CREATE)
//    void insertPeoplePersistPersistService1kBindValues(TestParam param) {
//        multipleRunner.execJob(1000,"PS_1K_BindValues", param,
//                (t) -> fastInsertService
//                        .insertPeoplePersistPersistService(
//                                param.batchSize(),
//                                1000,
//                                param.people()));
//        assertTest(param);
//    }

    @ParameterizedTest
    @MethodSource(NO_OF_PEOPLE_TO_CREATE)
    void insertPeoplePersistPersistService2kBindValues(TestParam param) {
        multipleRunner.execJob(2000,"PS_2K_BindValues", param,
                (t) -> fastInsertService
                        .insertPeoplePersistPersistService(
                                param.batchSize(),
                                2000,
                                param.people()));
        assertTest(param);
    }

    private void assertTest(TestParam param) {
        long count = personRepository.count();
        Assertions.assertThat(count).isEqualTo(param.people().size());
    }
    public static Stream<TestParam> noOfPeopleToCreate() {
        List<TestParam> params = new ArrayList<>();
        Date startDate = new Date();
//        int[] noOfPeople = {10, 1000, 10_000, 100_000, 1_000_000};
//        int[] batchSizes = {100, 500, 1_000, 10_000};

        int[] noOfPeople = {10_000_000};
        int[] batchSizes = {10_000};

        for (int noOfPerson : noOfPeople) {
            List<Person> people = RandomPersonGenerator.generateRandomPersons(noOfPerson);
            for (int batchSize : batchSizes) {
                params.add(new TestParam(people, batchSize, startDate));
            }
        }
        return params.stream();
    }

    public record TestParam(List<Person> people, int batchSize, Date testStartDate) { }

}
