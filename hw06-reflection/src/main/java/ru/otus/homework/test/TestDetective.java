package ru.otus.homework.test;

import java.util.Collection;

public interface TestDetective {

    /**
     * Ищем классы тэсты
     *
     * @return Классы которые тэстируем
     */
    Collection<Class> search();
}
