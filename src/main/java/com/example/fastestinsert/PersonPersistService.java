package com.example.fastestinsert;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.Types;
import java.util.Arrays;
import java.util.List;

@RequiredArgsConstructor
@Service
public class PersonPersistService {
    private final BulkPersistCustomRepository bulkPersistCustomRepository;
    private final SpringTemplateBulkInsertRepository springTemplateBulkInsertRepository;
    private final BulkCopyRepository bulkCopyRepository;
    private static final TableDescriptor<Person> PERSON_TABLE_DESCRIPTOR =
            TableDescriptor.of(PersonConstants.PERSON_TABLE_NAME,
                    Arrays.asList(
                            ColumnDescriptor.of(PersonConstants.PERSON_ID, PersonConstants.PERSON_ID_COL_DEF, Types.INTEGER, Person::getPersonId),
//                            ColumnDescriptor.of(PersonConstants.CREATION_DATE, PersonConstants.CREATION_DATE_COL_DEF, Types.DATE, person -> new java.sql.Date(person.getCreationDate().getTime())),
                            ColumnDescriptor.of(PersonConstants.FIRST_NAME, PersonConstants.FIRST_NAME_COL_DEF, Types.NVARCHAR,Person::getFirstName),
                            ColumnDescriptor.of(PersonConstants.LAST_NAME, PersonConstants.LAST_NAME_COL_DEF, Types.NVARCHAR, Person::getLastName),
                            ColumnDescriptor.of(PersonConstants.USER_NAME, PersonConstants.USERNAME_COL_DEF, Types.NVARCHAR, Person::getUserName),
                            ColumnDescriptor.of(PersonConstants.YEARS, PersonConstants.YEARS_COL_DEF, Types.INTEGER, Person::getYears)
                    ), List.of(PersonConstants.PERSON_ID));

    @Transactional
    public void persistPeople(List<Person> people, int batchSize, int objectsPerInsert) {
        bulkPersistCustomRepository.insert(people, PERSON_TABLE_DESCRIPTOR, batchSize, objectsPerInsert);
    }

    @Transactional
    public void persistPeopleSpringTemplate(List<Person> people, int batchSize, int objectsPerInsert) {
        springTemplateBulkInsertRepository.insertPeople(PERSON_TABLE_DESCRIPTOR, people, batchSize, objectsPerInsert);
    }

    @Transactional
    public void persistPeopleBulkApi(List<Person> people, int batchSize) {
        bulkCopyRepository.performBulkInsert(people, PERSON_TABLE_DESCRIPTOR, batchSize);
    }

}
