package com.example.fastestinsert;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor(staticName = "of")
public class TableDescriptor<T> {
    private final String tableName;
    private final List<ColumnDescriptor<T>> columns;
    private final List<String> pkColumns;
}
