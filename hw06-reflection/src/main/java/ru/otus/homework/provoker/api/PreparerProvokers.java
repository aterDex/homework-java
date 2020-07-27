package ru.otus.homework.provoker.api;

/**
 * Готовим класс теста к запуску
 */
public interface PreparerProvokers {

    ProvokerClass prepare(Class clazz) throws PreparerProvokersException;
}
