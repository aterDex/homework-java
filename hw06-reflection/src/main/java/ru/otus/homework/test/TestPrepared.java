package ru.otus.homework.test;

import java.lang.reflect.Method;
import java.util.Collection;

public interface TestPrepared {
    Class getClazz();

    Method getBeforeAll();

    Method getAfterAll();

    Method getBefore();

    Method getAfter();

    Collection<Method> getTests();

    boolean isValid();

    String getProblemsDescription();
}
