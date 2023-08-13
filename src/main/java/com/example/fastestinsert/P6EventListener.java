package com.example.fastestinsert;

import com.p6spy.engine.common.PreparedStatementInformation;
import com.p6spy.engine.common.StatementInformation;
import com.p6spy.engine.event.JdbcEventListener;
import org.springframework.stereotype.Component;

@Component
public class P6EventListener extends JdbcEventListener {

    @Override
    public void onBeforeAddBatch(PreparedStatementInformation statementInformation) {

    }

    @Override
    public void onBeforeExecuteBatch(StatementInformation statementInformation) {
        int length = statementInformation.getSqlWithValues().length();
        String sql = statementInformation.getSql();
        System.out.println("statementInformation = " + statementInformation.getSqlWithValues());
        super.onBeforeExecuteBatch(statementInformation);
    }
}
