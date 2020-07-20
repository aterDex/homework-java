package ru.otus.homework.test;

import java.util.List;
import java.util.Optional;

public interface TestClass {

    Class getClazz();

    String getDescription();

    Optional<Throwable> beforeAll();

    Optional<Throwable> afterAll();

    boolean isBeforeAll();

    boolean isAfterAll();

    List<TestClassMethod> getTestMethods();
}
