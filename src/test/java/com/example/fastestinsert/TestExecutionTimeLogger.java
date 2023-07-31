package com.example.fastestinsert;


import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.BeforeTestExecutionCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Slf4j
@Component
public class TestExecutionTimeLogger implements BeforeTestExecutionCallback, AfterTestExecutionCallback {

    private long startTime;

    @Override
    public void beforeTestExecution(ExtensionContext context) {
        startTime = System.currentTimeMillis();
    }

    @Override
    public void afterTestExecution(ExtensionContext context) {

        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;

        Method testMethod = context
                .getTestMethod()
                .orElseThrow(() -> new RuntimeException("test method not found"));

        // Get test method parameters (if any)
        Object[] parameters = testMethod.getParameterTypes();

        log.info("Test '" + context.getDisplayName() + " params: " + parameters[0] + "' executed in " + duration + " ms");
    }
}
