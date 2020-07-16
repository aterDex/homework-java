package ru.otus.homework.test;

import java.util.Collection;

public interface TestExecutor<T extends TestResult> {

    /**
     * Выполняем все тэсты в классе и возвращаем результат
     *
     * @param aClass класс с тэстами
     * @return Результаты тэстов
     */
    Collection<T> execute(Class aClass);
}
