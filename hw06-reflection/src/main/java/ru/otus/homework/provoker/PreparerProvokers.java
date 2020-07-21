package ru.otus.homework.provoker;

/**
 * Готовим класс теста к запуску
 */
public interface PreparerProvokers {

    ProvokerClass prepare(Class clazz) throws PreparerProvokersException;
}
