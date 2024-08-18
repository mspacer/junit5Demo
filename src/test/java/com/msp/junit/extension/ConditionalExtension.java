package com.msp.junit.extension;

import org.junit.jupiter.api.extension.ConditionEvaluationResult;
import org.junit.jupiter.api.extension.ExecutionCondition;
import org.junit.jupiter.api.extension.ExtensionContext;

public class ConditionalExtension implements ExecutionCondition {
    @Override
    public ConditionEvaluationResult evaluateExecutionCondition(ExtensionContext extensionContext) {
        System.out.println("evaluate Execution method: " + extensionContext.getTestMethod().orElse(null));
        if (System.getProperty("skip") != null &&
                extensionContext.getTestClass().get().getName().toLowerCase().indexOf(System.getProperty("skip")) >= 0) {
            return ConditionEvaluationResult.disabled("login test system disabled");
        }
        return ConditionEvaluationResult.enabled("enable");
    }
}
