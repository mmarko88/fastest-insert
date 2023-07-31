package com.example.fastestinsert;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.DecimalFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Service
@Transactional
@RequiredArgsConstructor
public class FastInsertService {
    @PersistenceContext
    private EntityManager entityManager;

    private final PersonPersistService personPersistService;

    private static final DecimalFormat decimalFormat = new DecimalFormat("#,###.###");


    private static Date generateRandomDate(Date minDate, Date maxDate) {
        long minMillis = minDate.getTime();
        long maxMillis = maxDate.getTime();
        long randomMillis = ThreadLocalRandom.current().nextLong(minMillis, maxMillis);
        return new Date(randomMillis);
    }


    private final PersonRepository personRepository;

    public void cleanUpPeopleTable() {
        entityManager.createNativeQuery("truncate table person").executeUpdate();
        entityManager.clear();
    }

    public void insertPeopleSaveAllBatched(List<Person> people) {
        personRepository.saveAll(people);
        entityManager.flush();
        entityManager.clear();
    }

    public void insertPeoplePersist(List<Person> people) {
        for (Person p : people) {
            entityManager.persist(p);
        }

        entityManager.flush();
        entityManager.clear();

    }

    public void insertPeoplePersistInChunks(int chunkSize, List<Person> people) {
        for (int i = 0; i < people.size(); i++) {
            Person p = people.get(i);
            entityManager.persist(p);
            if ((i + 1) % chunkSize == 0) {
                entityManager.flush();
                entityManager.clear();
            }
        }

        entityManager.flush();
        entityManager.clear();
    }

    public void insertPeoplePersistPersistService(int batchSize, int maxBindValues, List<Person> people) {
        personPersistService.persistPeople(people, batchSize, maxBindValues);
    }

}
