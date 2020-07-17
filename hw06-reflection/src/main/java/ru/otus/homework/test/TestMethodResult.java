package ru.otus.homework.test;

public interface TestMethodResult {

    /**
     * @return Короткое имя теста
     */
    String getName();

    /**
     *
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
     * @return исключение которое вызвал тест
     */
    Throwable getThrowable();
}
