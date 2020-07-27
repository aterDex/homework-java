package ru.otus.homework.provoker.impl;

import ru.otus.homework.provoker.api.*;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Базовая реализация ранера для тестов
 */
public class ProvokerFrameworkRunnerBase implements ProvokerFrameworkRunner {

    private Detective detective;
    private Executor executor;
    private ResultHandler resultHandler;

    public ProvokerFrameworkRunnerBase(Detective detective, Executor executor, ResultHandler resultHandler) {
        this.detective = detective;
        this.executor = executor;
        this.resultHandler = resultHandler;
    }

    public Detective getDetective() {
        return detective;
    }

    public void setDetective(Detective detective) {
        this.detective = detective;
    }

    public Executor getExecutor() {
        return executor;
    }

    public void setExecutor(Executor executor) {
        this.executor = executor;
    }

    public ResultHandler getResultHandler() {
        return resultHandler;
    }

    public void setResultHandler(ResultHandler resultHandler) {
        this.resultHandler = resultHandler;
    }

    @Override
    public void run() {
        Collection<Class> findingClass = detective.search();
        List<ProvokerClassResult> testsResult = findingClass.stream()
                .map(executor::execute)
                .collect(Collectors.toList());
        resultHandler.print(testsResult);
    }
}
