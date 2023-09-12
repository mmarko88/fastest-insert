package com.example.fastestinsert;

import java.util.List;

public interface BulkPersistCustomRepository {
    <T> void insert(List<T> objects, TableDescriptor<T> descriptor, int MAX_BIND_VALUES, int BATCH_SIZE);
}
