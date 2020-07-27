package ru.otus.homework.provoker.api;

import java.util.Collection;

public interface Detective {

    /**
     * Ищем классы тэсты
     *
     * @return Классы которые тэстируем
     */
    Collection<Class> search();
}
