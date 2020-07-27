package ru.otus.homework.provoker.api;

import java.util.Collection;

public interface ResultHandler {

    /**
     * Выводим результаты тэстов
     *
     * @param results результаты тэстов
     */
    void print(Collection<? extends ProvokerClassResult> results);
}
