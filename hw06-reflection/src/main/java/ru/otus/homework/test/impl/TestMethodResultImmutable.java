package ru.otus.homework.test.impl;

import ru.otus.homework.test.TestMethodResult;
import ru.otus.homework.test.TestResultEnum;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class TestMethodResultImmutable implements TestMethodResult {

    private final String name;
    private final String methodName;
    private final String description;
    private final TestResultEnum result;
    private final List<Throwable> throwable;

    TestMethodResultImmutable(String name, String methodName, String description, TestResultEnum result, List<? extends Throwable> throwable) {
        this.name = name;
        this.methodName = methodName;
        this.description = description;
        this.result = result;
        this.throwable = throwable == null ? Collections.emptyList() : Collections.unmodifiableList(new ArrayList<>(throwable));
    }

    public static TestMethodResultImmutableBuilder builder() {
        return new TestMethodResultImmutableBuilder();
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getMethodName() {
        return this.methodName;
    }

    @Override
    public String getDescription() {
        return this.description;
    }

    @Override
    public TestResultEnum getResult() {
        return this.result;
    }

    @Override
    public List<Throwable> getThrowable() {
        return this.throwable;
    }

    public static class TestMethodResultImmutableBuilder {
        private String name;
        private String methodName;
        private String description;
        private TestResultEnum result;
        private List<Throwable> throwable;

        TestMethodResultImmutableBuilder() {
        }

        public TestMethodResultImmutable.TestMethodResultImmutableBuilder name(String name) {
            this.name = name;
            return this;
        }

        public TestMethodResultImmutable.TestMethodResultImmutableBuilder methodName(String methodName) {
            this.methodName = methodName;
            return this;
        }

        public TestMethodResultImmutable.TestMethodResultImmutableBuilder description(String description) {
            this.description = description;
            return this;
        }

        public TestMethodResultImmutable.TestMethodResultImmutableBuilder result(TestResultEnum result) {
            this.result = result;
            return this;
        }

        public TestMethodResultImmutable.TestMethodResultImmutableBuilder throwable(List<Throwable> throwable) {
            this.throwable = throwable;
            return this;
        }

        public TestMethodResultImmutable build() {
            return new TestMethodResultImmutable(name, methodName, description, result, throwable);
        }
    }
}
