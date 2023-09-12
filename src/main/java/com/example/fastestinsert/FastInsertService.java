package com.example.fastestinsert;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private final ChunkEntityPersistRepository chunkEntityPersistRepository;

    public static Date generateRandomDate(Date minDate, Date maxDate) {
        long minMillis = minDate.getTime();
        long maxMillis = maxDate.getTime();
        long randomMillis = ThreadLocalRandom.current().nextLong(minMillis, maxMillis);
        return new Date(randomMillis);
    }

    public void cleanUpPeopleTable() {
        entityManager
                .createNativeQuery("truncate table person")
                .executeUpdate();

        entityManager.clear();
    }

    public void insertPeopleBulkInsert(int batchSize, int objectsPerInsert, List<Person> people) {
        personPersistService.persistPeople(people, batchSize, objectsPerInsert);
    }

    public void insertPeopleSpringTemplateBulkInsert(int batchSize, int objectsPerInsert, List<Person> people) {
        personPersistService.persistPeopleSpringTemplate(people, batchSize, objectsPerInsert);
    }

    public void persistPeopleBulkApi(List<Person> people, int batchSize) {
        personPersistService.persistPeopleBulkApi(people, batchSize);
    }

    public void persistPeopleHibernateFlush(List<Person> people, int batchSize) {
        chunkEntityPersistRepository.persistAndFlushInChunks(people, batchSize);
    }

}
