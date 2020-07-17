package ru.otus.homework.test;

import java.util.Collection;

public interface TestExecutor {

    /**
     * Выполняем все тесты в классе и возвращаем результат
     *
     * @param aClass класс с тестами
     * @return Результаты тестов
     */
    TestClassResult execute(Class aClass);
}
