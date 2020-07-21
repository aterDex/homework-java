package ru.otus.homework.provoker;

import java.util.List;
import java.util.Optional;

public interface ProvokerClass {

    Class getClazz();

    String getDescription();

    Optional<Throwable> beforeAll();

    Optional<Throwable> afterAll();

    boolean isBeforeAll();

    boolean isAfterAll();

    List<ProvokerClassMethod> getTestMethods();
}
