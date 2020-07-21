package ru.otus.homework.provoker.impl;

import ru.otus.homework.provoker.*;

public class RunnerImpl implements Runner {

    private Detective detective;
    private Executor executor;
    private Printer printer;

    public RunnerImpl(Detective detective, Executor executor, Printer printer) {
        this.detective = detective;
        this.executor = executor;
        this.printer = printer;
    }

    @Override
    public void run() {
//        Collection<Class> testClass = testDetective.search();
//        List<TestResult> testResult = testClass.stream()
//                .map(testExecutor::execute)
//                .flatMap(Collection::stream)
//                .collect(Collectors.toList());
//        testPrinter.print(testResult);
    }
}
