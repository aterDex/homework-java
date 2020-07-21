package ru.otus.homework.provoker;

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
