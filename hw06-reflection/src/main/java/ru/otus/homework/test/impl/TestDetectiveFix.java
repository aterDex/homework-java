package ru.otus.homework.test.impl;

import ru.otus.homework.test.TestDetective;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

/**
 * Фиксированный набор классов для тэстирования
 */
public class TestDetectiveFix implements TestDetective {

    private Collection<Class> classes;

    public TestDetectiveFix(Collection<? extends Class> classes) {
        this.classes = classes == null ?
                Collections.emptyList() :
                Collections.unmodifiableCollection(new ArrayList<>(classes));
    }

    @Override
    public Collection<Class> search() {
        return classes;
    }
}
