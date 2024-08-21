package com.msp.junit.validator;

public interface Validator<T> {

    ValidationResult validate(T object);
}
