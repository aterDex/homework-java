package ru.otus.homework.test;

import java.util.List;

public interface TestClass {

    Class getClazz();

    String getDescription();

    void beforeAll();

    void afterAll();

    List<TestClassMethod> getTestMethods();
}
