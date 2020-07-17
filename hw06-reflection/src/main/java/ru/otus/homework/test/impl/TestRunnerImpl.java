package ru.otus.homework.test.impl;

import ru.otus.homework.test.*;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class TestRunnerImpl implements TestRunner {

    private TestDetective testDetective;
    private TestExecutor testExecutor;
    private TestPrinter testPrinter;

    public TestRunnerImpl(TestDetective testDetective, TestExecutor testExecutor, TestPrinter testPrinter) {
        this.testDetective = testDetective;
        this.testExecutor = testExecutor;
        this.testPrinter = testPrinter;
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
