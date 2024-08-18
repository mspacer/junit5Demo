package com.msp.junit.extension;

import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

public class GlobalExtension implements BeforeAllCallback, AfterAllCallback {
    @Override
    public void beforeAll(ExtensionContext extensionContext) throws Exception {
        System.out.println("GlobalExtension beforeAllCallback: " + extensionContext.getTestClass().orElse(null));
    }

    @Override
    public void afterAll(ExtensionContext extensionContext) throws Exception {
        System.out.println("GlobalExtension afterAllCallback" + extensionContext.getTestClass().orElse(null));
    }
}
