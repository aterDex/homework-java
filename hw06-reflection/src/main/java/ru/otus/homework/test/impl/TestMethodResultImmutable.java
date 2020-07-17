package ru.otus.homework.test.impl;

import ru.otus.homework.test.TestMethodResult;
import ru.otus.homework.test.TestResultEnum;

public final class TestMethodResultImmutable implements TestMethodResult {

    private final String name;
    private final String methodName;
    private final String description;
    private final TestResultEnum result;
    private final Throwable throwable;

    TestMethodResultImmutable(String name, String methodName, String description, TestResultEnum result, Throwable throwable) {
        this.name = name;
        this.methodName = methodName;
        this.description = description;
        this.result = result;
        this.throwable = throwable;
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
    public Throwable getThrowable() {
        return this.throwable;
    }

    public static class TestMethodResultImmutableBuilder {
        private String name;
        private String methodName;
        private String description;
        private TestResultEnum result;
        private Throwable throwable;

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

        public TestMethodResultImmutable.TestMethodResultImmutableBuilder throwable(Throwable throwable) {
            this.throwable = throwable;
            return this;
        }

        public TestMethodResultImmutable build() {
            return new TestMethodResultImmutable(name, methodName, description, result, throwable);
        }
    }
}
