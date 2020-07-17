package ru.otus.homework.test.impl;

import ru.otus.homework.test.TestClassResult;
import ru.otus.homework.test.TestMethodResult;
import ru.otus.homework.test.TestResultEnum;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public final class TestClassResultImmutable implements TestClassResult {

    private final Class clazz;
    private final String name;
    private final String description;
    private final TestResultEnum result;
    private final Throwable throwable;
    private final String problemDescription;
    private final Collection<TestMethodResult> testMethodResults;

    TestClassResultImmutable(Class clazz, String name, String description, TestResultEnum result, Throwable throwable, String problemDescription, Collection<TestMethodResult> testMethodResults) {
        this.clazz = clazz;
        this.name = name;
        this.description = description;
        this.result = result;
        this.throwable = throwable;
        this.problemDescription = problemDescription;
        this.testMethodResults = testMethodResults;
    }

    public static TestClassResultImmutableBuilder builder() {
        return new TestClassResultImmutableBuilder();
    }

    @Override
    public Class getClazz() {
        return this.clazz;
    }

    @Override
    public String getName() {
        return this.name;
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

    @Override
    public String getProblemDescription() {
        return this.problemDescription;
    }

    @Override
    public Collection<TestMethodResult> getTestMethodResults() {
        return this.testMethodResults;
    }

    public static class TestClassResultImmutableBuilder {

        private Class clazz;
        private String name;
        private String description;
        private TestResultEnum result;
        private Throwable throwable;
        private String problemDescription;
        private Collection<TestMethodResult> testMethodResults;

        TestClassResultImmutableBuilder() {
        }

        public TestClassResultImmutable.TestClassResultImmutableBuilder clazz(Class clazz) {
            this.clazz = clazz;
            return this;
        }

        public TestClassResultImmutable.TestClassResultImmutableBuilder name(String name) {
            this.name = name;
            return this;
        }

        public TestClassResultImmutable.TestClassResultImmutableBuilder description(String description) {
            this.description = description;
            return this;
        }

        public TestClassResultImmutable.TestClassResultImmutableBuilder result(TestResultEnum result) {
            this.result = result;
            return this;
        }

        public TestClassResultImmutable.TestClassResultImmutableBuilder throwable(Throwable throwable) {
            this.throwable = throwable;
            return this;
        }

        public TestClassResultImmutable.TestClassResultImmutableBuilder problemDescription(String problemDescription) {
            this.problemDescription = problemDescription;
            return this;
        }

        public TestClassResultImmutable.TestClassResultImmutableBuilder testMethodResults(Collection<TestMethodResult> testMethodResults) {
            this.testMethodResults = testMethodResults;
            return this;
        }

        public TestClassResultImmutable build() {
            return new TestClassResultImmutable(clazz, name, description, result, throwable, problemDescription,
                    testMethodResults == null ?
                            Collections.emptyList() :
                            Collections.unmodifiableCollection(new ArrayList<>(testMethodResults)));
        }
    }
}
