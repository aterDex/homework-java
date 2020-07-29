package ru.otus.homework.provoker.api;

import java.util.List;

public interface ProvokerClassMethodResult {

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
    ProvocationResultEnum getResult();

    /**
     * @return исключения которое вызвал тест
     */
    List<Throwable> getThrowable();

    /**
     * @return Описание результата теста
     */
    String getDescriptionResult();
}
