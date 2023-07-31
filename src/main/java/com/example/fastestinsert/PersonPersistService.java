package com.example.fastestinsert;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@RequiredArgsConstructor
@Service
public class PersonPersistService {
    private final TempTablePersistService<Person> tempTablePersistService;
    private static final TableDescriptor<Person> PERSON_TABLE_DESCRIPTOR =
            TableDescriptor.of(PersonConstants.PERSON_TABLE_NAME,
                    Arrays.asList(
                            ColumnDescriptor.of(PersonConstants.PERSON_ID, PersonConstants.PERSON_ID_COL_DEF, Person::getPersonId),
                            ColumnDescriptor.of(PersonConstants.USER_NAME, PersonConstants.USERNAME_COL_DEF, Person::getUserName),
                            ColumnDescriptor.of(PersonConstants.FIRST_NAME, PersonConstants.FIRST_NAME_COL_DEF, Person::getFirstName),
                            ColumnDescriptor.of(PersonConstants.LAST_NAME, PersonConstants.LAST_NAME_COL_DEF, Person::getLastName),
//                            ColumnDescriptor.of(PersonConstants.CREATION_DATE, PersonConstants.CREATION_DATE_COL_DEF, Person::getCreationDate),
                            ColumnDescriptor.of(PersonConstants.YEARS, PersonConstants.YEARS_COL_DEF, Person::getYears)
                    ), List.of(PersonConstants.PERSON_ID));

    @Transactional
    public void persistPeople(List<Person> people, double BATCH_SIZE, int MAX_BIND_VALUES) {
        tempTablePersistService.insertEntities(people, PERSON_TABLE_DESCRIPTOR, MAX_BIND_VALUES, BATCH_SIZE);
    }
}
