package com.example.fastestinsert;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(staticName = "of")
public class TempTableDescriptor<T> {
    private final TableDescriptor<T> tableDescriptor;
    private final String tempTableName;
}
