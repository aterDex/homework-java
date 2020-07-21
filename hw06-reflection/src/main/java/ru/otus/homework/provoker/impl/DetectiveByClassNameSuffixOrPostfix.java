package ru.otus.homework.provoker.impl;

import ru.otus.homework.provoker.Detective;

import java.util.Collection;

/**
 * Ищет классы тэстов по их имени (суфикс или постфикс, в определенном пакете)
 */
public class DetectiveByClassNameSuffixOrPostfix implements Detective {

    @Override
    public Collection<Class> search() {
        throw new UnsupportedOperationException();
    }
}
