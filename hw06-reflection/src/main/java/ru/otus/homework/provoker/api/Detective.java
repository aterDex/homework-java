package ru.otus.homework.provoker.api;

import java.util.Collection;

public interface Detective {

    /**
     * Смотрим классы тэсты
     *
     * @return Классы которые тэстируем
     */
    Collection<Class<?>> search();
}
