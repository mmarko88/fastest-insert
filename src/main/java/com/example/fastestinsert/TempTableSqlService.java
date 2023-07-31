package com.example.fastestinsert;

import com.google.common.base.Strings;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.stream.Collectors;


@Service
public class TempTableSqlService {
    public String getDropTableSql(TempTableDescriptor<?> descriptor) {
        return "DROP TABLE " + descriptor.getTempTableName();
    }

    public String getInsertQuery(TempTableDescriptor<?> descriptor, int valueBindingRepetitions) {
        TableDescriptor<?> tableDescriptor = descriptor.getTableDescriptor();
        return "INSERT INTO " +
                tableDescriptor.getTableName() +
                " (" +
                getColumnsCommaSeparated(tableDescriptor) +
                ")" +
                "VALUES " +
                repeatCommaSeparated(valueBindingRepetitions, "(" +
                        repeatCommaSeparated(tableDescriptor.getColumns().size(), "?") + ")");
    }

    public String getUpdateOriginalTable(TempTableDescriptor<?> descriptor) {
        String sourceTableAlias = " t";
        TableDescriptor<?> tableDescriptor = descriptor.getTableDescriptor();

        return "UPDATE " +
                tableDescriptor.getTableName() +
                " SET " +
                getSetAllColumnValues(tableDescriptor, sourceTableAlias) +
                " FROM " +
                descriptor.getTempTableName() +
                sourceTableAlias +
                " WHERE " +
                getPkJoinConditions(tableDescriptor, sourceTableAlias);
    }

    private static String getPkJoinConditions(TableDescriptor<?> descriptor, String sourceTableAlias) {
        return
                descriptor
                        .getPkColumns()
                        .stream()
                        .map(pkColName -> sourceTableAlias + "." + pkColName +
                                "=" +
                                descriptor.getTableName() + "." + pkColName)
                        .collect(Collectors.joining(" AND "));
    }

    private static String getSetAllColumnValues(TableDescriptor<?> descriptor, String sourceTableAlias) {
        return
                descriptor
                        .getColumns()
                        .stream()
                        .map(cd -> cd.getColumnName() + "=" + sourceTableAlias + "." + cd.getColumnName())
                        .collect(Collectors.joining(","));
    }

    public String getInsertIntoOriginalTable(TempTableDescriptor<?> descriptor) {
        TableDescriptor<?> tableDescriptor = descriptor.getTableDescriptor();
        return "INSERT INTO " +
                tableDescriptor.getTableName() +
                " (" +
                getColumnsCommaSeparated(tableDescriptor) +
                ") SELECT " +
                getColumnsCommaSeparated(tableDescriptor) +
                " FROM " +
                descriptor.getTempTableName();
    }

    private static String getColumnsCommaSeparated(TableDescriptor<?> descriptor) {
        return
                descriptor
                        .getColumns()
                        .stream()
                        .map(ColumnDescriptor::getColumnName)
                        .collect(Collectors.joining(","));
    }

    public static String repeatCommaSeparated(int count, String string) {
        String values = Strings.repeat(string + ",", count);
        return values.substring(0, values.length() - 1);
    }

    public String getCreateTable(TempTableDescriptor<?> descriptor) {
        TableDescriptor<?> tableDescriptor = descriptor.getTableDescriptor();
        return "CREATE TABLE " +
                descriptor.getTempTableName() +
                " (" +
                getColumnNamesWithDbType(tableDescriptor) +
                (CollectionUtils.isEmpty(tableDescriptor.getPkColumns()) ? "" :
                        " CONSTRAINT [PK_" +
                                descriptor.getTempTableName() +
                                "] PRIMARY KEY CLUSTERED (" +
                                getPkColumnsCommaSeparated(tableDescriptor) +
                                "))");
    }

    private static String getPkColumnsCommaSeparated(TableDescriptor<?> descriptor) {
        return String.join(",", descriptor.getPkColumns());
    }

    private static String getColumnNamesWithDbType(TableDescriptor<?> descriptor) {
        return
                descriptor
                        .getColumns()
                        .stream()
                        .map(col -> col.getColumnName() + " " + col.getDbSqlType())
                        .collect(Collectors.joining(","));
    }
}
