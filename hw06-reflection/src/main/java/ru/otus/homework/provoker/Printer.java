package ru.otus.homework.provoker;

import java.util.Collection;

public interface Printer {

    /**
     * Выводим результаты тэстов
     *
     * @param results результаты тэстов
     */
    void print(Collection<? extends ProvokerClassResult> results);
}
