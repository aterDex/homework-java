package ru.otus.homework.test;

public interface TestResult {

    /**
     * @return Тэстируемый класс
     */
    Class getClazz();

    /**
     * @return Короткое имя тэста
     */
    String getName();

    /**
     * @return Описания тэста
     */
    String getDescription();

    /**
     * @return результат тэста
     */
    TestResultEnum getResult();

    /**
     * @return исключение которое вызвал тэст
     */
    Throwable getThrowable();
}
