package com.example.fastestinsert;

import lombok.Getter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
public class DataTable {
    private final List<String> columnNames;
    private final Map<String, Class<?>> columnTypes;
    private final List<DataRow> rows;

    public DataTable() {
        this.columnNames = new ArrayList<>();
        this.columnTypes = new HashMap<>();
        this.rows = new ArrayList<>();
    }

    public void addColumn(String columnName, Class<?> columnType) {
        columnNames.add(columnName);
        columnTypes.put(columnName, columnType);
    }

    public void add(DataRow dataRow) {
        rows.add(dataRow);
    }

    public static class DataRow {
        private final Map<String, Object> values;

        public DataRow() {
            this.values = new HashMap<>();
        }

        public void set(String columnName, Object value) {
            values.put(columnName, value);
        }

        public Object get(String columnName) {
            return values.get(columnName);
        }
    }
}
