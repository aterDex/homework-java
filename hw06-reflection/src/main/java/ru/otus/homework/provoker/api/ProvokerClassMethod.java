package ru.otus.homework.provoker.api;

import java.util.Optional;

public interface ProvokerClassMethod {

    String getDescription();

    String getMethodName();

    Optional<Throwable> init();

    Optional<Throwable> before();

    Optional<Throwable> test();

    Optional<Throwable> after();

    boolean isAfter();

    boolean isBefore();
}
