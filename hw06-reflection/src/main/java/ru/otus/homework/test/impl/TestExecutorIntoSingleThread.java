package ru.otus.homework.test.impl;

import ru.otus.homework.test.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TestExecutorIntoSingleThread implements TestExecutor {

    private final TestClassPreparer testClassPreparer;

    public TestExecutorIntoSingleThread(TestClassPreparer testClassPreparer) {
        this.testClassPreparer = testClassPreparer;
    }

    @Override
    public TestClassResult execute(Class aClass) {
        TestClass testClass;
        try {
            testClass = testClassPreparer.prepare(aClass);
        } catch (Throwable t) {
            return createIllegalResult(t, aClass);
        }

        List<TestMethodResult> methodsResults = new ArrayList<>(testClass.getTestMethods().size());
        Optional<Throwable> beforeAll, afterAll;

        beforeAll = testClass.beforeAll();
        if (!beforeAll.isEmpty()) {
            methodsResults.addAll(createAllSkipTestMethodResult(testClass));
        } else {
            for (TestClassMethod testMethod : testClass.getTestMethods()) {
                Optional<Throwable> init, before, after, test;
                init = testMethod.init();
                before = testMethod.before();
                test = before.isEmpty() ? testMethod.test() : Optional.empty();
                after = testMethod.after();
                methodsResults.add(createMethodResult(testMethod, init, before, test, after));
            }
        }
        afterAll = testClass.afterAll();
        return createResult(beforeAll, afterAll, testClass, methodsResults);
    }

    private Collection<TestMethodResult> createAllSkipTestMethodResult(TestClass testClass) {
        return testClass.getTestMethods().stream()
                .map(x -> TestMethodResultImmutable
                        .builder()
                        .methodName(x.getMethodName())
                        .description(testClass.getDescription())
                        .result(TestResultEnum.SKIP)
                        .build()
                ).collect(Collectors.toList());
    }

    private TestMethodResult createMethodResult(
            TestClassMethod testMethod,
            Optional<Throwable> init,
            Optional<Throwable> before,
            Optional<Throwable> test,
            Optional<Throwable> after) {

        TestMethodResultImmutable.TestMethodResultImmutableBuilder builder = TestMethodResultImmutable.builder();
        builder
                .methodName(testMethod.getMethodName())
                .description(testMethod.getDescription());

        List<Throwable> listThrow = Stream.of(init, before, test, after)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());

        if (listThrow.isEmpty()) {
            builder
                    .result(TestResultEnum.OK);
        } else {
            builder
                    .result(TestResultEnum.FAILED)
                    .throwable(listThrow);
        }
        return builder.build();
    }

    private TestClassResult createResult(Optional<Throwable> beforeAll, Optional<Throwable> afterAll, TestClass testClass, List<TestMethodResult> results) {
        List<Throwable> listThrow = Stream.of(beforeAll, afterAll)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
        TestResultEnum result =
                !listThrow.isEmpty() || results.stream()
                        .filter(x -> x.getResult() == TestResultEnum.FAILED)
                        .findAny().isPresent()
                        ? TestResultEnum.FAILED : TestResultEnum.OK;

        return TestClassResultImmutable.builder()
                .clazz(testClass.getClazz())
                .name(testClass.getClazz().getName())
                .description(testClass.getDescription())
                .result(result)
                .testMethodResults(results == null ? createAllSkipTestMethodResult(testClass) : results)
                .throwable(listThrow)
                .build();
    }

    private TestClassResult createIllegalResult(Throwable throwable, Class aClass) {
        return TestClassResultImmutable.builder()
                .clazz(aClass)
                .name(aClass.getName())
                .throwable(List.of(throwable))
                .result(TestResultEnum.ILLEGAL)
                .build();
    }
}