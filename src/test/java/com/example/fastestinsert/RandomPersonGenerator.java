package com.example.fastestinsert;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class RandomPersonGenerator {
    private static int personId = 0;
    public static List<Person> generateRandomPersons(int noOfPeople) {
        List<Person> people = new ArrayList<>(noOfPeople);
        for (int i = 0; i < noOfPeople; i++) {
            people.add(genPerson());
        }
        return people;
    }
    private static Person genPerson() {
        Calendar minDate = Calendar.getInstance();
        minDate.set(1900, Calendar.JANUARY, 1);

        return new Person(++personId,
                RandomStringUtils.randomAlphanumeric(PersonConstants.USERNAME_LENGTH),
                RandomStringUtils.randomAlphabetic(PersonConstants.FIRST_NAME_LENGTH),
                RandomStringUtils.randomAlphabetic(PersonConstants.LAST_NAME_LENGTH),
                RandomUtils.nextInt(10, 100)
//                generateRandomDate(minDate.getTime(),new Date())
        );
    }
}
