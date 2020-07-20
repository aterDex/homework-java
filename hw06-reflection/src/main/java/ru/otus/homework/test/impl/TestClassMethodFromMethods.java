package ru.otus.homework.test.impl;

import ru.otus.homework.test.TestClassMethod;

import java.lang.reflect.Method;
import java.util.Optional;

public class TestClassMethodFromMethods implements TestClassMethod {

    private final String description;
    private final Method before;
    private final Method test;
    private final Method after;
    private final Class calzz;
    private Object instance;

    public TestClassMethodFromMethods(Class clazz, String description, Method before, Method test, Method after) {
        if (test == null) throw new NullPointerException("Не задан метод для тестирования");
        this.description = description;
        this.before = before;
        this.test = test;
        this.after = after;
        this.calzz = clazz;
    }

    @Override
    public Optional<Throwable> init() {
        try {
            instance = calzz.getDeclaredConstructor().newInstance();
        } catch (Throwable t) {
            return Optional.of(t);
        }
        return Optional.empty();
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public String getMethodName() {
        return test.getName();
    }

    @Override
    public Optional<Throwable> before() {
        return InvokeUtil.invokeMethod(before, instance);
    }

    @Override
    public Optional<Throwable> test() {
        return InvokeUtil.invokeMethod(test, instance);
    }

    @Override
    public Optional<Throwable> after() {
        return InvokeUtil.invokeMethod(after, instance);
    }
}
