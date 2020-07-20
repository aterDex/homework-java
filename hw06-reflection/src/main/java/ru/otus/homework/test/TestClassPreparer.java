package ru.otus.homework.test;

/**
 * Готовим класс теста к запуску
 */
public interface TestClassPreparer {

    TestClass prepare(Class clazz) throws TestClassPreparerException;
}
