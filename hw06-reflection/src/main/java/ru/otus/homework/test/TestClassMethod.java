package ru.otus.homework.test;

import java.util.Optional;

public interface TestClassMethod {

    String getDescription();

    String getMethodName();

    Optional<Throwable> init();

    Optional<Throwable> before();

    Optional<Throwable> test();

    Optional<Throwable> after();
}
