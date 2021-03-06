package ru.otus.homework.provoker.api;

public interface Executor {

    /**
     * Выполняем все тесты в классе и возвращаем результат
     *
     * @param aClass класс с тестами
     * @return Результаты тестов
     */
    ProvokerClassResult execute(Class<?> aClass);
}
