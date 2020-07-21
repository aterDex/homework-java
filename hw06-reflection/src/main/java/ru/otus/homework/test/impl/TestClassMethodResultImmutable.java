package ru.otus.homework.test.impl;

import ru.otus.homework.test.TestClassMethodResult;
import ru.otus.homework.test.TestResultEnum;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class TestClassMethodResultImmutable implements TestClassMethodResult {

    private final String methodName;
    private final String description;
    private final TestResultEnum result;
    private final String descriptionResult;
    private final List<Throwable> throwable;

    TestClassMethodResultImmutable(String methodName, String description, TestResultEnum result, List<? extends Throwable> throwable, String descriptionResult) {
        this.descriptionResult = descriptionResult;
        this.methodName = methodName;
        this.description = description;
        this.result = result;
        this.throwable = throwable == null ? Collections.emptyList() : Collections.unmodifiableList(new ArrayList<>(throwable));
    }

    public static TestMethodResultImmutableBuilder builder() {
        return new TestMethodResultImmutableBuilder();
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

    @Override
    public String getDescriptionResult() {
        return this.descriptionResult;
    }

    public static class TestMethodResultImmutableBuilder {

        private String descriptionResult;
        private String methodName;
        private String description;
        private TestResultEnum result;
        private List<Throwable> throwable;

        TestMethodResultImmutableBuilder() {
        }

        public TestClassMethodResultImmutable.TestMethodResultImmutableBuilder descriptionResult(String descriptionResult) {
            this.descriptionResult = descriptionResult;
            return this;
        }

        public TestClassMethodResultImmutable.TestMethodResultImmutableBuilder methodName(String methodName) {
            this.methodName = methodName;
            return this;
        }

        public TestClassMethodResultImmutable.TestMethodResultImmutableBuilder description(String description) {
            this.description = description;
            return this;
        }

        public TestClassMethodResultImmutable.TestMethodResultImmutableBuilder result(TestResultEnum result) {
            this.result = result;
            return this;
        }

        public TestClassMethodResultImmutable.TestMethodResultImmutableBuilder throwable(List<Throwable> throwable) {
            this.throwable = throwable;
            return this;
        }

        public TestClassMethodResultImmutable build() {
            return new TestClassMethodResultImmutable(methodName, description, result, throwable, descriptionResult);
        }
    }
}
