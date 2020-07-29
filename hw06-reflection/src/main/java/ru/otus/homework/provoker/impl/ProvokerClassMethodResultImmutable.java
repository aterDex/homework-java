package ru.otus.homework.provoker.impl;

import ru.otus.homework.provoker.api.ProvocationResultEnum;
import ru.otus.homework.provoker.api.ProvokerClassMethodResult;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class ProvokerClassMethodResultImmutable implements ProvokerClassMethodResult {

    private final String methodName;
    private final String description;
    private final ProvocationResultEnum result;
    private final String descriptionResult;
    private final List<Throwable> throwable;

    ProvokerClassMethodResultImmutable(String methodName, String description, ProvocationResultEnum result, List<? extends Throwable> throwable, String descriptionResult) {
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
    public ProvocationResultEnum getResult() {
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
        private ProvocationResultEnum result;
        private List<Throwable> throwable;

        TestMethodResultImmutableBuilder() {
        }

        public ProvokerClassMethodResultImmutable.TestMethodResultImmutableBuilder descriptionResult(String descriptionResult) {
            this.descriptionResult = descriptionResult;
            return this;
        }

        public ProvokerClassMethodResultImmutable.TestMethodResultImmutableBuilder methodName(String methodName) {
            this.methodName = methodName;
            return this;
        }

        public ProvokerClassMethodResultImmutable.TestMethodResultImmutableBuilder description(String description) {
            this.description = description;
            return this;
        }

        public ProvokerClassMethodResultImmutable.TestMethodResultImmutableBuilder result(ProvocationResultEnum result) {
            this.result = result;
            return this;
        }

        public ProvokerClassMethodResultImmutable.TestMethodResultImmutableBuilder throwable(List<Throwable> throwable) {
            this.throwable = throwable;
            return this;
        }

        public ProvokerClassMethodResultImmutable build() {
            return new ProvokerClassMethodResultImmutable(methodName, description, result, throwable, descriptionResult);
        }
    }
}
