package ru.otus.homework.test.impl;

import ru.otus.homework.test.TestClass;
import ru.otus.homework.test.TestClassMethod;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class TestClassFromMethods implements TestClass {

    private final Class clazz;
    private final String description;
    private final Method beforeAll;
    private final Method afterAll;
    private final List<TestClassMethod> testClassMethods;

    public TestClassFromMethods(Class clazz, String description, Method beforeAll, Method afterAll, List<? extends TestClassMethod> testClassMethods) {
        this.clazz = clazz;
        this.description = description;
        this.beforeAll = beforeAll;
        this.afterAll = afterAll;
        this.testClassMethods = Collections.unmodifiableList(new ArrayList<>(testClassMethods));
    }

    @Override
    public Class getClazz() {
        return clazz;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public Optional<Throwable> beforeAll() {
        return InvokeUtil.invokeMethod(beforeAll, null);
    }

    @Override
    public Optional<Throwable> afterAll() {
        return InvokeUtil.invokeMethod(afterAll, null);
    }

    @Override
    public List<TestClassMethod> getTestMethods() {
        return testClassMethods;
    }
}
