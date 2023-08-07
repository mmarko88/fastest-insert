package com.example.fastestinsert;

import com.p6spy.engine.common.*;
import com.p6spy.engine.event.CompoundJdbcEventListener;
import com.p6spy.engine.event.JdbcEventListener;
import com.p6spy.engine.spy.P6Factory;
import com.p6spy.engine.spy.P6LoadableOptions;
import com.p6spy.engine.spy.option.P6OptionsRepository;

import java.sql.SQLException;

public class CustomPerformanceModule implements P6Factory {


    @Override
    public P6LoadableOptions getOptions(P6OptionsRepository optionsRepository) {
        return null;
    }

    @Override
    public JdbcEventListener getJdbcEventListener() {
        return new JdbcEventListener() {

            @Override
            public void onAfterExecute(PreparedStatementInformation statementInformation, long timeElapsedNanos, SQLException e) {
                logExecution(statementInformation, timeElapsedNanos);
                super.onAfterExecute(statementInformation, timeElapsedNanos, e);
            }

            @Override
            public void onAfterExecute(StatementInformation statementInformation, long timeElapsedNanos, String sql, SQLException e) {
                logExecution(statementInformation, timeElapsedNanos);
                super.onAfterExecute(statementInformation, timeElapsedNanos, sql, e);
            }

            @Override
            public void onAfterExecuteBatch(StatementInformation statementInformation, long timeElapsedNanos, int[] updateCounts, SQLException e) {
                logExecution(statementInformation, timeElapsedNanos);
                super.onAfterExecuteBatch(statementInformation, timeElapsedNanos, updateCounts, e);
            }

            @Override
            public void onAfterExecuteUpdate(PreparedStatementInformation statementInformation, long timeElapsedNanos, int rowCount, SQLException e) {
                logExecution(statementInformation, timeElapsedNanos);
                super.onAfterExecuteUpdate(statementInformation, timeElapsedNanos, rowCount, e);
            }
            @Override
            public void onBeforeExecuteUpdate(StatementInformation statementInformation, String sql) {
                super.onBeforeExecuteUpdate(statementInformation, sql);
            }

            @Override
            public void onAfterExecuteUpdate(StatementInformation statementInformation, long timeElapsedNanos, String sql, int rowCount, SQLException e) {
                logExecution(statementInformation, timeElapsedNanos);
                super.onAfterExecuteUpdate(statementInformation, timeElapsedNanos, sql, rowCount, e);
            }

            @Override
            public void onBeforeExecuteQuery(PreparedStatementInformation statementInformation) {
                super.onBeforeExecuteQuery(statementInformation);
            }

            @Override
            public void onAfterExecuteQuery(PreparedStatementInformation statementInformation, long timeElapsedNanos, SQLException e) {
                super.onAfterExecuteQuery(statementInformation, timeElapsedNanos, e);
            }

            @Override
            public void onBeforeExecuteQuery(StatementInformation statementInformation, String sql) {
                super.onBeforeExecuteQuery(statementInformation, sql);
            }

            @Override
            public void onAfterExecuteQuery(StatementInformation statementInformation, long timeElapsedNanos, String sql, SQLException e) {
                super.onAfterExecuteQuery(statementInformation, timeElapsedNanos, sql, e);
            }

            @Override
            public void onAfterPreparedStatementSet(PreparedStatementInformation statementInformation, int parameterIndex, Object value, SQLException e) {
                super.onAfterPreparedStatementSet(statementInformation, parameterIndex, value, e);
            }

            @Override
            public void onAfterCallableStatementSet(CallableStatementInformation statementInformation, String parameterName, Object value, SQLException e) {
                super.onAfterCallableStatementSet(statementInformation, parameterName, value, e);
            }

            @Override
            public void onAfterGetResultSet(StatementInformation statementInformation, long timeElapsedNanos, SQLException e) {
                super.onAfterGetResultSet(statementInformation, timeElapsedNanos, e);
            }

            @Override
            public void onBeforeResultSetNext(ResultSetInformation resultSetInformation) {
                super.onBeforeResultSetNext(resultSetInformation);
            }

            @Override
            public void onAfterResultSetNext(ResultSetInformation resultSetInformation, long timeElapsedNanos, boolean hasNext, SQLException e) {
                super.onAfterResultSetNext(resultSetInformation, timeElapsedNanos, hasNext, e);
            }

            @Override
            public void onAfterResultSetClose(ResultSetInformation resultSetInformation, SQLException e) {
                super.onAfterResultSetClose(resultSetInformation, e);
            }

            @Override
            public void onAfterResultSetGet(ResultSetInformation resultSetInformation, String columnLabel, Object value, SQLException e) {
                super.onAfterResultSetGet(resultSetInformation, columnLabel, value, e);
            }

            @Override
            public void onAfterResultSetGet(ResultSetInformation resultSetInformation, int columnIndex, Object value, SQLException e) {
                super.onAfterResultSetGet(resultSetInformation, columnIndex, value, e);
            }

            @Override
            public void onBeforeCommit(ConnectionInformation connectionInformation) {
                System.out.println("Commit starting");
                super.onBeforeCommit(connectionInformation);
            }

            @Override
            public void onAfterCommit(ConnectionInformation connectionInformation, long timeElapsedNanos, SQLException e) {
                System.out.println("Commit completed");
                super.onAfterCommit(connectionInformation, timeElapsedNanos, e);
            }

            @Override
            public void onAfterConnectionClose(ConnectionInformation connectionInformation, SQLException e) {
                System.out.println("Connection closed");
                super.onAfterConnectionClose(connectionInformation, e);
            }

            @Override
            public void onBeforeRollback(ConnectionInformation connectionInformation) {
                super.onBeforeRollback(connectionInformation);
            }

            @Override
            public void onAfterRollback(ConnectionInformation connectionInformation, long timeElapsedNanos, SQLException e) {
                super.onAfterRollback(connectionInformation, timeElapsedNanos, e);
            }
        };
    }

    private static void logExecution(StatementInformation statementInformation, long timeElapsedNanos) {
        System.out.println("TimeElapsed:" + timeElapsedNanos);
        System.out.println("statementInformation.getSql() = " + statementInformation.getSql());
    }
}
