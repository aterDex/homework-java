package ru.otus.homework.provoker.api;

public enum ProvocationResultEnum {

    /**
     * Тест успешно выполнен
     */
    OK,
    /**
     * Тест провален
     */
    FAILED,
    /**
     * Тест пропущен
     */
    SKIP,
    /**
     * Тест не валидный
     */
    ILLEGAL
}
