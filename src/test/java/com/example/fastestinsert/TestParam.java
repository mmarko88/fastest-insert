package com.example.fastestinsert;

import lombok.Value;

import java.util.Date;
import java.util.List;

@Value
public class TestParam {

    List<Person> people;
    int batchSize;
    Date testStartDate;
    int objectsPerInsert;

}
