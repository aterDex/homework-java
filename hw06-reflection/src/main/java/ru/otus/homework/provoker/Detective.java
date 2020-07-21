package ru.otus.homework.provoker;

import java.util.Collection;

public interface Detective {

    /**
     * Ищем классы тэсты
     *
     * @return Классы которые тэстируем
     */
    Collection<Class> search();
}
