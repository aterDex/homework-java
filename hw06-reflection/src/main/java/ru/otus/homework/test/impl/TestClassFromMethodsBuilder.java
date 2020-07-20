package ru.otus.homework.test.impl;

import ru.otus.homework.test.TestClassMethod;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class TestClassFromMethodsBuilder {

    private final Class clazz;
    private String description;
    private Method beforeAll;
    private Method afterAll;
    private Method before;
    private Method after;
    private List<Method> methods = new ArrayList<>();
    private List<String> methodsDescription = new ArrayList<>();

    public TestClassFromMethodsBuilder(Class clazz) {
        this.clazz = clazz;
    }

    public Class getClazz() {
        return clazz;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Method getBeforeAll() {
        return beforeAll;
    }

    public void setBeforeAll(Method beforeAll) {
        this.beforeAll = beforeAll;
    }

    public Method getAfterAll() {
        return afterAll;
    }

    public void setAfterAll(Method afterAll) {
        this.afterAll = afterAll;
    }

    public Method getBefore() {
        return before;
    }

    public void setBefore(Method before) {
        this.before = before;
    }

    public Method getAfter() {
        return after;
    }

    public void setAfter(Method after) {
        this.after = after;
    }

    public void addTestMethod(String description, Method test) {
        methodsDescription.add(description);
        methods.add(test);
    }

    public TestClassFromMethods createTestClass() {
        return new TestClassFromMethods(clazz, description, beforeAll, afterAll, createTestMethodsClass());
    }

    public int getTestMethodCount() {
        return methods.size();
    }

    private List<TestClassMethod> createTestMethodsClass() {
        List<TestClassMethod> testClassMethods = new ArrayList<>(methods.size());
        for (int i = 0; i < methods.size(); i++) {
            testClassMethods.add(new TestClassMethodFromMethods(clazz, methodsDescription.get(i), before, methods.get(i), after));
        }
        return testClassMethods;
    }
}
