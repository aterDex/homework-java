package ru.otus.homework.test;

import java.util.List;

public interface TestClassMethodResult {

    /**
     * @return Метод теста
     */
    String getMethodName();

    /**
     * @return Описания теста
     */
    String getDescription();

    /**
     * @return результат теста
     */
    TestResultEnum getResult();

    /**
     * @return исключения которое вызвал тест
     */
    List<Throwable> getThrowable();

    /**
     * @return Описание результата теста
     */
    String getDescriptionResult();
}
