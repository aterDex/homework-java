package ru.otus.homework.test.impl;

import ru.otus.homework.test.TestClassMethod;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class TestClassFromMethods implements TestClassMethod {

    private final String description;
    private final Method before;
    private final Method test;
    private final Method after;
    private final Class calzz;
    private Object instance;

    public TestClassFromMethods(Class clazz, String description, Method before, Method test, Method after) {
        if (test == null) throw new NullPointerException("Не задан метод для тестирования");
        this.description = description;
        this.before = before;
        this.test = test;
        this.after = after;
        this.calzz = clazz;
    }

    @Override
    public void init() throws Throwable {
        instance = calzz.getDeclaredConstructor().newInstance();
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
    public void before() throws Throwable {
        if (before != null) {
            try {
                before.invoke(instance);
            } catch (InvocationTargetException ite) {
                throw ite.getCause();
            }
        }
    }

    @Override
    public void test() throws Throwable {

    }

    @Override
    public void after() throws Throwable {

    }
}
