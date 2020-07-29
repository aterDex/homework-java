package ru.otus.homework.provoker.impl;

import ru.otus.homework.provoker.api.Detective;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

/**
 * Фиксированный набор классов для тестирования
 */
public class DetectiveFix implements Detective {

    private final Collection<Class<?>> classes;

    public DetectiveFix(Collection<? extends Class<?>> classes) {
        this.classes = classes == null ?
                Collections.emptyList() :
                Collections.unmodifiableCollection(new ArrayList<>(classes));
    }

    @Override
    public Collection<Class<?>> search() {
        return classes;
    }
}
