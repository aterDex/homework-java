package ru.otus.homework.provoker.impl;

import ru.otus.homework.provoker.*;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Базовая реализация ранера для тестов
 */
public class ProvokerFrameworkRunnerBase implements ProvokerFrameworkRunner {

    private Detective detective;
    private Executor executor;
    private Printer printer;

    public ProvokerFrameworkRunnerBase(Detective detective, Executor executor, Printer printer) {
        this.detective = detective;
        this.executor = executor;
        this.printer = printer;
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

    public Printer getPrinter() {
        return printer;
    }

    public void setPrinter(Printer printer) {
        this.printer = printer;
    }

    @Override
    public void run() {
        Collection<Class> findingClass = detective.search();
        List<ProvokerClassResult> testsResult = findingClass.stream()
                .map(executor::execute)
                .collect(Collectors.toList());
        printer.print(testsResult);
    }
}
