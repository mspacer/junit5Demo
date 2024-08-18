package com.msp.junit.paramresolver;

import com.msp.junit.service.UserService;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;

public class UserServiceParamResolver implements ParameterResolver {
    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return parameterContext.getParameter().getType().equals(UserService.class);
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        //поскольку класс - синглетон, можно создать переменную UserService и всегда возвращать один объект
        //или воспользоваться внутренним механизмом кеширования
        // каждый метод теста запускается в своем контексте, соответственно store тоже разный
        // поэтому UserService в каждом методе все равно будет разный, а
        // beforeEach + тестовый метод - одинаковый
        var store =  extensionContext.getStore(ExtensionContext.Namespace.GLOBAL);
        return store.getOrComputeIfAbsent(UserService.class, userServiceClass -> new UserService());
        //return new UserService();
    }
}
