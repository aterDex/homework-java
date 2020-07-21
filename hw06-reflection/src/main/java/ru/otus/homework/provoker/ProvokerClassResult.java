package ru.otus.homework.provoker;

import java.util.Collection;
import java.util.List;

public interface ProvokerClassResult {

    /**
     * @return Тестируемый класс
     */
    Class getClazz();

    /**
     * @return Описания теста
     */
    String getDescription();

    /**
     * @return результат теста
     */
    ProvocationResultEnum getResult();

    /**
     * @return исключение которое вызвал тест
     */
    List<Throwable> getThrowable();

    /**
     * @return Описание проблемы
     */
    String getDescriptionResult();

    /**
     * Результаты тестирования по методам
     *
     * @return
     */
    Collection<ProvokerClassMethodResult> getTestMethodResults();
}
