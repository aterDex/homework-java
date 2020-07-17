package ru.otus.homework.test;

/**
 * Готовим класс тэста к запуску
 */
public interface TestPreparer {

    TestPrepared prepare(Class clazz);
}
