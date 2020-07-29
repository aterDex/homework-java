package ru.otus.homework.provoker.impl;

import ru.otus.homework.provoker.api.ProvocationResultEnum;
import ru.otus.homework.provoker.api.ProvokerClassMethodResult;
import ru.otus.homework.provoker.api.ProvokerClassResult;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public final class ProvokerClassResultImmutable implements ProvokerClassResult {

    private final Class<?> clazz;
    private final String description;
    private final ProvocationResultEnum result;
    private final List<Throwable> throwable;
    private final String descriptionResult;
    private final Collection<ProvokerClassMethodResult> testMethodResults;

    ProvokerClassResultImmutable(Class<?> clazz, String description, ProvocationResultEnum result, List<Throwable> throwable, String descriptionResult, Collection<ProvokerClassMethodResult> testMethodResults) {
        this.clazz = clazz;
        this.description = description;
        this.result = result;
        this.throwable = throwable == null ?
                Collections.emptyList() :
                Collections.unmodifiableList(new ArrayList<>(throwable));
        this.descriptionResult = descriptionResult;
        this.testMethodResults = testMethodResults == null ?
                Collections.emptyList() :
                Collections.unmodifiableCollection(new ArrayList<>(testMethodResults));
    }

    public static TestClassResultImmutableBuilder builder() {
        return new TestClassResultImmutableBuilder();
    }

    @Override
    public Class<?> getClazz() {
        return this.clazz;
    }

    @Override
    public String getDescription() {
        return this.description;
    }

    @Override
    public ProvocationResultEnum getResult() {
        return this.result;
    }

    @Override
    public List<Throwable> getThrowable() {
        return this.throwable;
    }

    @Override
    public Collection<ProvokerClassMethodResult> getTestMethodResults() {
        return this.testMethodResults;
    }

    @Override
    public String getDescriptionResult() {
        return this.descriptionResult;
    }

    public static class TestClassResultImmutableBuilder {

        private Class<?> clazz;
        private String name;
        private String description;
        private ProvocationResultEnum result;
        private List<Throwable> throwable;
        private String problemDescription;
        private Collection<ProvokerClassMethodResult> testMethodResults;

        TestClassResultImmutableBuilder() {
        }

        public ProvokerClassResultImmutable.TestClassResultImmutableBuilder clazz(Class<?> clazz) {
            this.clazz = clazz;
            return this;
        }

        public ProvokerClassResultImmutable.TestClassResultImmutableBuilder name(String name) {
            this.name = name;
            return this;
        }

        public ProvokerClassResultImmutable.TestClassResultImmutableBuilder description(String description) {
            this.description = description;
            return this;
        }

        public ProvokerClassResultImmutable.TestClassResultImmutableBuilder result(ProvocationResultEnum result) {
            this.result = result;
            return this;
        }

        public ProvokerClassResultImmutable.TestClassResultImmutableBuilder throwable(List<Throwable> throwable) {
            this.throwable = throwable;
            return this;
        }

        public ProvokerClassResultImmutable.TestClassResultImmutableBuilder problemDescription(String problemDescription) {
            this.problemDescription = problemDescription;
            return this;
        }

        public ProvokerClassResultImmutable.TestClassResultImmutableBuilder testMethodResults(Collection<ProvokerClassMethodResult> testMethodResults) {
            this.testMethodResults = testMethodResults;
            return this;
        }

        public ProvokerClassResultImmutable build() {
            return new ProvokerClassResultImmutable(clazz, description, result,
                    throwable,
                    problemDescription,
                    testMethodResults);
        }
    }
}
