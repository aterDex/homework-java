package ru.otus.homework.test;

import java.util.Collection;

public interface TestClassResult {

    /**
     * @return Тестируемый класс
     */
    Class getClazz();

    /**
     * @return Короткое имя теста
     */
    String getName();

    /**
     * @return Описания теста
     */
    String getDescription();

    /**
     * @return результат теста
     */
    TestResultEnum getResult();

    /**
     * @return исключение которое вызвал тест
     */
    Throwable getThrowable();

    /**
     * @return Описание проблемы
     */
    String getProblemDescription();

    /**
     * Результаты тестирования по методам
     *
     * @return
     */
    Collection<TestMethodResult> getTestMethodResults();
}
