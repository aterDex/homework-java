package ru.otus.homework.test.impl;

import ru.otus.homework.test.TestClassMethodResult;
import ru.otus.homework.test.TestClassResult;
import ru.otus.homework.test.TestResultEnum;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public final class TestClassResultImmutable implements TestClassResult {

    private final Class clazz;
    private final String description;
    private final TestResultEnum result;
    private final List<Throwable> throwable;
    private final String descriptionResult;
    private final Collection<TestClassMethodResult> testMethodResults;

    TestClassResultImmutable(Class clazz, String description, TestResultEnum result, List<Throwable> throwable, String descriptionResult, Collection<TestClassMethodResult> testMethodResults) {
        this.clazz = clazz;
        this.description = description;
        this.result = result;
        this.throwable = throwable;
        this.descriptionResult = descriptionResult;
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
    public Collection<TestClassMethodResult> getTestMethodResults() {
        return this.testMethodResults;
    }

    @Override
    public String getDescriptionResult() {
        return this.descriptionResult;
    }

    public static class TestClassResultImmutableBuilder {

        private Class clazz;
        private String name;
        private String description;
        private TestResultEnum result;
        private List<Throwable> throwable;
        private String problemDescription;
        private Collection<TestClassMethodResult> testMethodResults;

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

        public TestClassResultImmutable.TestClassResultImmutableBuilder throwable(List<Throwable> throwable) {
            this.throwable = throwable;
            return this;
        }

        public TestClassResultImmutable.TestClassResultImmutableBuilder problemDescription(String problemDescription) {
            this.problemDescription = problemDescription;
            return this;
        }

        public TestClassResultImmutable.TestClassResultImmutableBuilder testMethodResults(Collection<TestClassMethodResult> testMethodResults) {
            this.testMethodResults = testMethodResults;
            return this;
        }

        public TestClassResultImmutable build() {
            return new TestClassResultImmutable(clazz, description, result,
                    throwable == null ?
                            Collections.emptyList() :
                            Collections.unmodifiableList(new ArrayList<>(throwable)),
                    problemDescription,
                    testMethodResults == null ?
                            Collections.emptyList() :
                            Collections.unmodifiableCollection(new ArrayList<>(testMethodResults)));
        }
    }
}
