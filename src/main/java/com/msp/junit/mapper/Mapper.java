package com.msp.junit.mapper;

public interface Mapper<F, T> {

    T map(F object);
}
