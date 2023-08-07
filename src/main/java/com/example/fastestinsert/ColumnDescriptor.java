package com.example.fastestinsert;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.sql.Types;
import java.util.function.Function;

@Getter
@RequiredArgsConstructor(staticName = "of")
public class ColumnDescriptor<T> {
    private final String columnName;
    private final String dbSqlType;
    private final int sqlType;
    private final Function<T, Object> entityFieldMapper;

    public Object getEntityFieldValue(T entity) {
        return entityFieldMapper.apply(entity);
    }
}
