package ru.otus.homework.test.impl;

import ru.otus.homework.test.TestDetective;

import java.util.Collection;

/**
 * Ищет классы тэстов по их имени (суфикс или постфикс, в определенном пакете)
 */
public class TestDetectiveByClassNameSuffixOrPostfix implements TestDetective {

    @Override
    public Collection<Class> search() {
        throw new UnsupportedOperationException();
    }
}
