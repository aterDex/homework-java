package ru.otus.homework.test.impl;

import ru.otus.homework.test.*;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class TestExecutorIntoSingleThread implements TestExecutor {

    private final TestPreparer testPreparer;

    public TestExecutorIntoSingleThread(TestPreparer testPreparer) {
        this.testPreparer = testPreparer;
    }

    public TestPreparer getTestPreparer() {
        return testPreparer;
    }

    @Override
    public TestClassResult execute(Class aClass) {
        TestPrepared preparedTest = testPreparer.prepare(aClass);
        if (!preparedTest.isValid()) {
            return createIllegalResult(preparedTest);
        }
        if (preparedTest.getBeforeAll() != null) {
            try {
                preparedTest.getBeforeAll().invoke(null);
            } catch (InvocationTargetException ite) {
                return createErrorResult(ite.getTargetException(), "Произошла ошибка при вызове метода @BeforeAll", preparedTest, null);
            } catch (Throwable e) {
                return createErrorResult(e, "Произошла ошибка при вызове метода @BeforeAll", preparedTest, null);
            }
        }
        List<TestMethodResult> results = new ArrayList<>();
        for (Method test : preparedTest.getTests()) {
            results.add(executeTest(preparedTest.getClazz(), preparedTest.getBefore(), test, preparedTest.getAfter()));
        }
        if (preparedTest.getAfterAll() != null) {
            try {
                preparedTest.getBeforeAll().invoke(null);
            } catch (InvocationTargetException ite) {
                return createErrorResult(ite.getTargetException(), "Произошла ошибка при вызове метода @AfterAll", preparedTest, null);
            } catch (Throwable e) {
                return createErrorResult(e, "Произошла ошибка при вызове метода @AfterAll", preparedTest, results);
            }
        }
        return createOkResult(results);
    }

    private TestMethodResult executeTest(Class clazz, Method before, Method test, Method after) {
        Object instance;
        try {
            Constructor constructor = clazz.getDeclaredConstructor();
            instance = constructor.newInstance();
        } catch (Throwable t) {
            throw new UnsupportedOperationException();
        }
        if (before != null) {
            try {
                before.invoke(instance);
            } catch (InvocationTargetException ite) {
                throw new UnsupportedOperationException();
            } catch (Throwable e) {
                throw new UnsupportedOperationException();
            }
        }
        try {
            test.invoke(instance);
        } catch (InvocationTargetException ite) {
            throw new UnsupportedOperationException();
        } catch (Throwable e) {
            throw new UnsupportedOperationException();
        }
        if (after != null) {
            try {
                after.invoke(instance);
            } catch (InvocationTargetException ite) {
                throw new UnsupportedOperationException();
            } catch (Throwable e) {
                throw new UnsupportedOperationException();
            }
        }
        throw new UnsupportedOperationException();
    }

    private TestClassResult createOkResult(List<TestMethodResult> results) {
        return null;
    }

    private TestClassResult createErrorResult(Throwable e, String description, TestPrepared preparedTest, List<TestMethodResult> results) {
        return TestClassResultImmutable.builder()
                .clazz(preparedTest.getClazz())
                .name(preparedTest.getClazz().getName())
                .description(description)
                .throwable(e)
                .result(TestResultEnum.FAILED)
                .testMethodResults(results == null ? createAllSkipTestMethodResult(preparedTest) : results)
                .build();
    }

    private Collection<TestMethodResult> createAllSkipTestMethodResult(TestPrepared preparedTest) {
        return preparedTest.getTests().stream()
                .map(x -> TestMethodResultImmutable
                        .builder()
                        .methodName(x.getName())
                        .name(x.getName())
                        .result(TestResultEnum.SKIP)
                        .build()
                ).collect(Collectors.toList());
    }

    private TestClassResult createIllegalResult(TestPrepared preparedTest) {
        return TestClassResultImmutable.builder()
                .clazz(preparedTest.getClazz())
                .name(preparedTest.getClazz().getName())
                .problemDescription(preparedTest.getProblemsDescription())
                .result(TestResultEnum.ILLEGAL)
                .build();
    }
}