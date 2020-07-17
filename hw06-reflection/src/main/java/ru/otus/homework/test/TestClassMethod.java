package ru.otus.homework.test;

public interface TestClassMethod {

    String getDescription();

    String getMethodName();

    void init() throws Throwable;

    void before() throws Throwable;

    void test() throws Throwable;

    void after() throws Throwable;
}
