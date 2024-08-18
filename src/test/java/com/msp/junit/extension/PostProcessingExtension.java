package com.msp.junit.extension;

import com.msp.junit.service.Includetest;
import com.msp.junit.service.UtilUnit;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestInstancePostProcessor;

import java.lang.reflect.Field;

public class PostProcessingExtension implements TestInstancePostProcessor {
    @Override
    public void postProcessTestInstance(Object testInstance, ExtensionContext extensionContext) throws Exception {
        System.out.println("PostProcessingExtension exec");
        Field[] declaredFields = testInstance.getClass().getDeclaredFields();
        for( Field field: declaredFields) {
            if(field.isAnnotationPresent(Includetest.class)) {
                field.setAccessible(true);
                //field.getType().getConstructors()[0].
                field.set(testInstance, new UtilUnit()/*field.getType().newInstance()*/ );
            }
        }
    }
}
