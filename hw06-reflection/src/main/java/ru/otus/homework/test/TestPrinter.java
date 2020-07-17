package ru.otus.homework.test;

import java.util.Collection;

public interface TestPrinter {

    /**
     * Выводим результаты тэстов
     *
     * @param results результаты тэстов
     */
    void print(Collection<? extends TestClassResult> results);
}
