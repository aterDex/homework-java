package ru.otus.homework.test.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.otus.homework.test.*;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TestExecutorIntoSingleThreadTest {

    private TestExecutorIntoSingleThread executor;
    @Mock
    private TestClassPreparer mockPreparer;
    @Mock
    private TestClass mockTestClass;
    @Mock
    private TestClassMethod mockTestClassMethod0;
    @Mock
    private TestClassMethod mockTestClassMethod1;
    @Mock
    private TestClassMethod mockTestClassMethod2;

    private Class testClass;

    @BeforeEach
    void before() {
        testClass = TestExecutorIntoSingleThreadTest.class;
        executor = new TestExecutorIntoSingleThread(mockPreparer);
    }

    @ParameterizedTest
    @ValueSource(classes = {
            TestClassPreparerException.class,
            NullPointerException.class
    })
    void executeException(Class exception) {
        when(mockPreparer.prepare(testClass)).thenThrow(exception);
        TestClassResult result = executor.execute(testClass);
        assertNotNull(result);
        assertEquals(1, result.getThrowable().size());
        assertEquals(0, result.getTestMethodResults().size());
        assertEquals(TestResultEnum.ILLEGAL, result.getResult());
        assertEquals(testClass, result.getClazz());
        assertEquals(exception, result.getThrowable().get(0).getClass());
    }

    @Test
    void executeAllMethodOk() {
        when(mockPreparer.prepare(testClass)).thenReturn(mockTestClass);
        when(mockTestClass.getTestMethods()).thenReturn(List.of(mockTestClassMethod0, mockTestClassMethod1, mockTestClassMethod2));
        when(mockTestClass.getClazz()).thenReturn(testClass);

        InOrder inOrder = inOrder(mockTestClass, mockTestClassMethod0, mockTestClassMethod1, mockTestClassMethod2);

        TestClassResult result = executor.execute(testClass);
        assertNotNull(result);
        assertEquals(testClass, result.getClazz());
        assertEquals(TestResultEnum.OK, result.getResult());
        assertEquals(3, result.getTestMethodResults().size());
        assertEquals(0, result.getThrowable().size());
        for (TestMethodResult testMethodResult : result.getTestMethodResults()) {
            assertEquals(TestResultEnum.OK, testMethodResult.getResult());
            assertEquals(0, testMethodResult.getThrowable().size());
        }

        inOrder.verify(mockTestClass).beforeAll();
        inOrder.verify(mockTestClassMethod0).init();
        inOrder.verify(mockTestClassMethod0).before();
        inOrder.verify(mockTestClassMethod0).test();
        inOrder.verify(mockTestClassMethod0).after();
        inOrder.verify(mockTestClassMethod1).init();
        inOrder.verify(mockTestClassMethod1).before();
        inOrder.verify(mockTestClassMethod1).test();
        inOrder.verify(mockTestClassMethod1).after();
        inOrder.verify(mockTestClassMethod2).init();
        inOrder.verify(mockTestClassMethod2).before();
        inOrder.verify(mockTestClassMethod2).test();
        inOrder.verify(mockTestClassMethod2).after();
        inOrder.verify(mockTestClass).afterAll();
    }

    @Test
    void executeMethodTestFailed() {
        String exceptionMethodName = "ExceptionTest";

        NullPointerException testException = new NullPointerException();
        when(mockPreparer.prepare(testClass)).thenReturn(mockTestClass);
        when(mockTestClass.getTestMethods()).thenReturn(List.of(mockTestClassMethod0, mockTestClassMethod1, mockTestClassMethod2));
        when(mockTestClassMethod1.test()).thenReturn(Optional.of(testException));
        when(mockTestClassMethod1.getMethodName()).thenReturn(exceptionMethodName);
        when(mockTestClass.getClazz()).thenReturn(testClass);

        InOrder inOrder = inOrder(mockTestClass, mockTestClassMethod0, mockTestClassMethod1, mockTestClassMethod2);

        TestClassResult result = executor.execute(testClass);
        assertNotNull(result);
        assertEquals(testClass, result.getClazz());
        assertEquals(TestResultEnum.FAILED, result.getResult());
        assertEquals(3, result.getTestMethodResults().size());
        assertEquals(0, result.getThrowable().size());
        for (TestMethodResult testMethodResult : result.getTestMethodResults()) {
            if (exceptionMethodName.equals(testMethodResult.getMethodName())) {
                assertEquals(TestResultEnum.FAILED, testMethodResult.getResult());
                assertEquals(1, testMethodResult.getThrowable().size());
            } else {
                assertEquals(TestResultEnum.OK, testMethodResult.getResult());
                assertEquals(0, testMethodResult.getThrowable().size());
            }
        }

        inOrder.verify(mockTestClass).beforeAll();
        inOrder.verify(mockTestClassMethod0).init();
        inOrder.verify(mockTestClassMethod0).before();
        inOrder.verify(mockTestClassMethod0).test();
        inOrder.verify(mockTestClassMethod0).after();
        inOrder.verify(mockTestClassMethod1).init();
        inOrder.verify(mockTestClassMethod1).before();
        inOrder.verify(mockTestClassMethod1).test();
        inOrder.verify(mockTestClassMethod1).after();
        inOrder.verify(mockTestClassMethod2).init();
        inOrder.verify(mockTestClassMethod2).before();
        inOrder.verify(mockTestClassMethod2).test();
        inOrder.verify(mockTestClassMethod2).after();
        inOrder.verify(mockTestClass).afterAll();
    }

    @Test
    void executeMethodBeforeTestFailed() {
        String exceptionMethodName = "ExceptionTest";

        NullPointerException testException = new NullPointerException();
        when(mockPreparer.prepare(testClass)).thenReturn(mockTestClass);
        when(mockTestClass.getTestMethods()).thenReturn(List.of(mockTestClassMethod0, mockTestClassMethod1, mockTestClassMethod2));
        when(mockTestClassMethod1.before()).thenReturn(Optional.of(testException));
        when(mockTestClassMethod1.getMethodName()).thenReturn(exceptionMethodName);
        when(mockTestClass.getClazz()).thenReturn(testClass);

        InOrder inOrder = inOrder(mockTestClass, mockTestClassMethod0, mockTestClassMethod1, mockTestClassMethod2);

        TestClassResult result = executor.execute(testClass);
        assertNotNull(result);
        assertEquals(testClass, result.getClazz());
        assertEquals(TestResultEnum.FAILED, result.getResult());
        assertEquals(3, result.getTestMethodResults().size());
        assertEquals(0, result.getThrowable().size());
        for (TestMethodResult testMethodResult : result.getTestMethodResults()) {
            if (exceptionMethodName.equals(testMethodResult.getMethodName())) {
                assertEquals(TestResultEnum.FAILED, testMethodResult.getResult());
                assertEquals(1, testMethodResult.getThrowable().size());
            } else {
                assertEquals(TestResultEnum.OK, testMethodResult.getResult());
                assertEquals(0, testMethodResult.getThrowable().size());
            }
        }

        inOrder.verify(mockTestClass).beforeAll();
        inOrder.verify(mockTestClassMethod0).init();
        inOrder.verify(mockTestClassMethod0).before();
        inOrder.verify(mockTestClassMethod0).test();
        inOrder.verify(mockTestClassMethod0).after();
        inOrder.verify(mockTestClassMethod1).init();
        inOrder.verify(mockTestClassMethod1).before();

        inOrder.verify(mockTestClassMethod1).after();
        inOrder.verify(mockTestClassMethod2).init();
        inOrder.verify(mockTestClassMethod2).before();
        inOrder.verify(mockTestClassMethod2).test();
        inOrder.verify(mockTestClassMethod2).after();
        inOrder.verify(mockTestClass).afterAll();
    }

    @Test
    void executeMethodAfterTestFailed() {
        String exceptionMethodName = "ExceptionTest";

        NullPointerException testException = new NullPointerException();
        when(mockPreparer.prepare(testClass)).thenReturn(mockTestClass);
        when(mockTestClass.getTestMethods()).thenReturn(List.of(mockTestClassMethod0, mockTestClassMethod1, mockTestClassMethod2));
        when(mockTestClassMethod1.after()).thenReturn(Optional.of(testException));
        when(mockTestClassMethod1.getMethodName()).thenReturn(exceptionMethodName);
        when(mockTestClass.getClazz()).thenReturn(testClass);

        InOrder inOrder = inOrder(mockTestClass, mockTestClassMethod0, mockTestClassMethod1, mockTestClassMethod2);

        TestClassResult result = executor.execute(testClass);
        assertNotNull(result);
        assertEquals(testClass, result.getClazz());
        assertEquals(TestResultEnum.FAILED, result.getResult());
        assertEquals(3, result.getTestMethodResults().size());
        assertEquals(0, result.getThrowable().size());
        for (TestMethodResult testMethodResult : result.getTestMethodResults()) {
            if (exceptionMethodName.equals(testMethodResult.getMethodName())) {
                assertEquals(TestResultEnum.FAILED, testMethodResult.getResult());
                assertEquals(1, testMethodResult.getThrowable().size());
            } else {
                assertEquals(TestResultEnum.OK, testMethodResult.getResult());
                assertEquals(0, testMethodResult.getThrowable().size());
            }
        }

        inOrder.verify(mockTestClass).beforeAll();
        inOrder.verify(mockTestClassMethod0).init();
        inOrder.verify(mockTestClassMethod0).before();
        inOrder.verify(mockTestClassMethod0).test();
        inOrder.verify(mockTestClassMethod0).after();
        inOrder.verify(mockTestClassMethod1).init();
        inOrder.verify(mockTestClassMethod1).before();
        inOrder.verify(mockTestClassMethod1).test();
        inOrder.verify(mockTestClassMethod1).after();
        inOrder.verify(mockTestClassMethod2).init();
        inOrder.verify(mockTestClassMethod2).before();
        inOrder.verify(mockTestClassMethod2).test();
        inOrder.verify(mockTestClassMethod2).after();
        inOrder.verify(mockTestClass).afterAll();
    }

    @Test
    void executeMethodBeforeAndAfterTestFailedAndTestSkip() {
        String exceptionMethodName = "ExceptionTest";

        NullPointerException testException = new NullPointerException();
        when(mockPreparer.prepare(testClass)).thenReturn(mockTestClass);
        when(mockTestClass.getTestMethods()).thenReturn(List.of(mockTestClassMethod0, mockTestClassMethod1, mockTestClassMethod2));
        when(mockTestClassMethod1.before()).thenReturn(Optional.of(testException));
        when(mockTestClassMethod1.after()).thenReturn(Optional.of(testException));
        when(mockTestClassMethod1.getMethodName()).thenReturn(exceptionMethodName);
        when(mockTestClass.getClazz()).thenReturn(testClass);

        InOrder inOrder = inOrder(mockTestClass, mockTestClassMethod0, mockTestClassMethod1, mockTestClassMethod2);

        TestClassResult result = executor.execute(testClass);
        assertNotNull(result);
        assertEquals(testClass, result.getClazz());
        assertEquals(TestResultEnum.FAILED, result.getResult());
        assertEquals(3, result.getTestMethodResults().size());
        assertEquals(0, result.getThrowable().size());
        for (TestMethodResult testMethodResult : result.getTestMethodResults()) {
            if (exceptionMethodName.equals(testMethodResult.getMethodName())) {
                assertEquals(TestResultEnum.FAILED, testMethodResult.getResult());
                assertEquals(2, testMethodResult.getThrowable().size());
            } else {
                assertEquals(TestResultEnum.OK, testMethodResult.getResult());
                assertEquals(0, testMethodResult.getThrowable().size());
            }
        }

        inOrder.verify(mockTestClass).beforeAll();
        inOrder.verify(mockTestClassMethod0).init();
        inOrder.verify(mockTestClassMethod0).before();
        inOrder.verify(mockTestClassMethod0).test();
        inOrder.verify(mockTestClassMethod0).after();
        inOrder.verify(mockTestClassMethod1).init();
        inOrder.verify(mockTestClassMethod1).before();

        inOrder.verify(mockTestClassMethod1).after();
        inOrder.verify(mockTestClassMethod2).init();
        inOrder.verify(mockTestClassMethod2).before();
        inOrder.verify(mockTestClassMethod2).test();
        inOrder.verify(mockTestClassMethod2).after();
        inOrder.verify(mockTestClass).afterAll();
    }

    @Test
    void executeMethodInitTestFailed() {
        String exceptionMethodName = "ExceptionTest";

        NullPointerException testException = new NullPointerException();
        when(mockPreparer.prepare(testClass)).thenReturn(mockTestClass);
        when(mockTestClass.getTestMethods()).thenReturn(List.of(mockTestClassMethod0, mockTestClassMethod1, mockTestClassMethod2));
        when(mockTestClassMethod1.init()).thenReturn(Optional.of(testException));
        when(mockTestClassMethod1.getMethodName()).thenReturn(exceptionMethodName);
        when(mockTestClass.getClazz()).thenReturn(testClass);

        InOrder inOrder = inOrder(mockTestClass, mockTestClassMethod0, mockTestClassMethod1, mockTestClassMethod2);

        TestClassResult result = executor.execute(testClass);
        assertNotNull(result);
        assertEquals(testClass, result.getClazz());
        assertEquals(TestResultEnum.FAILED, result.getResult());
        assertEquals(3, result.getTestMethodResults().size());
        assertEquals(0, result.getThrowable().size());
        for (TestMethodResult testMethodResult : result.getTestMethodResults()) {
            if (exceptionMethodName.equals(testMethodResult.getMethodName())) {
                assertEquals(TestResultEnum.FAILED, testMethodResult.getResult());
                assertEquals(1, testMethodResult.getThrowable().size());
            } else {
                assertEquals(TestResultEnum.OK, testMethodResult.getResult());
                assertEquals(0, testMethodResult.getThrowable().size());
            }
        }

        inOrder.verify(mockTestClass).beforeAll();
        inOrder.verify(mockTestClassMethod0).init();
        inOrder.verify(mockTestClassMethod0).before();
        inOrder.verify(mockTestClassMethod0).test();
        inOrder.verify(mockTestClassMethod0).after();
        inOrder.verify(mockTestClassMethod1).init();
        inOrder.verify(mockTestClassMethod2).init();
        inOrder.verify(mockTestClassMethod2).before();
        inOrder.verify(mockTestClassMethod2).test();
        inOrder.verify(mockTestClassMethod2).after();
        inOrder.verify(mockTestClass).afterAll();
    }

    @Test
    void executeBeforeAllException() {
        NullPointerException testException = new NullPointerException();

        when(mockPreparer.prepare(testClass)).thenReturn(mockTestClass);
        when(mockTestClass.getTestMethods()).thenReturn(List.of(mockTestClassMethod0, mockTestClassMethod1, mockTestClassMethod2));
        when(mockTestClass.beforeAll()).thenReturn(Optional.of(testException));
        when(mockTestClass.getClazz()).thenReturn(testClass);

        InOrder inOrder = inOrder(mockTestClass, mockTestClassMethod0, mockTestClassMethod1, mockTestClassMethod2);

        TestClassResult result = executor.execute(testClass);
        assertNotNull(result);
        assertEquals(testClass, result.getClazz());
        assertEquals(TestResultEnum.FAILED, result.getResult());
        assertEquals(3, result.getTestMethodResults().size());
        assertEquals(1, result.getThrowable().size());
        for (TestMethodResult testMethodResult : result.getTestMethodResults()) {
            assertEquals(TestResultEnum.SKIP, testMethodResult.getResult());
            assertEquals(0, testMethodResult.getThrowable().size());
        }

        inOrder.verify(mockTestClass).beforeAll();
        inOrder.verify(mockTestClass).afterAll();
    }

    @Test
    void executeAfterAllException() {
        NullPointerException testException = new NullPointerException();

        when(mockPreparer.prepare(testClass)).thenReturn(mockTestClass);
        when(mockTestClass.getTestMethods()).thenReturn(List.of(mockTestClassMethod0, mockTestClassMethod1, mockTestClassMethod2));
        when(mockTestClass.afterAll()).thenReturn(Optional.of(testException));
        when(mockTestClass.getClazz()).thenReturn(testClass);

        InOrder inOrder = inOrder(mockTestClass, mockTestClassMethod0, mockTestClassMethod1, mockTestClassMethod2);

        TestClassResult result = executor.execute(testClass);
        assertNotNull(result);
        assertEquals(testClass, result.getClazz());
        assertEquals(TestResultEnum.FAILED, result.getResult());
        assertEquals(3, result.getTestMethodResults().size());
        assertEquals(1, result.getThrowable().size());
        for (TestMethodResult testMethodResult : result.getTestMethodResults()) {
            assertEquals(TestResultEnum.OK, testMethodResult.getResult());
            assertEquals(0, testMethodResult.getThrowable().size());
        }

        inOrder.verify(mockTestClass).beforeAll();
        inOrder.verify(mockTestClassMethod0).init();
        inOrder.verify(mockTestClassMethod0).before();
        inOrder.verify(mockTestClassMethod0).test();
        inOrder.verify(mockTestClassMethod0).after();
        inOrder.verify(mockTestClassMethod1).init();
        inOrder.verify(mockTestClassMethod1).before();
        inOrder.verify(mockTestClassMethod1).test();
        inOrder.verify(mockTestClassMethod1).after();
        inOrder.verify(mockTestClassMethod2).init();
        inOrder.verify(mockTestClassMethod2).before();
        inOrder.verify(mockTestClassMethod2).test();
        inOrder.verify(mockTestClassMethod2).after();
        inOrder.verify(mockTestClass).afterAll();
    }

    @Test
    void executeBeforeAllAndAfterAllException() {
        NullPointerException testException = new NullPointerException();

        when(mockPreparer.prepare(testClass)).thenReturn(mockTestClass);
        when(mockTestClass.getTestMethods()).thenReturn(List.of(mockTestClassMethod0, mockTestClassMethod1, mockTestClassMethod2));
        when(mockTestClass.afterAll()).thenReturn(Optional.of(testException));
        when(mockTestClass.beforeAll()).thenReturn(Optional.of(testException));
        when(mockTestClass.getClazz()).thenReturn(testClass);

        InOrder inOrder = inOrder(mockTestClass, mockTestClassMethod0, mockTestClassMethod1, mockTestClassMethod2);

        TestClassResult result = executor.execute(testClass);
        assertNotNull(result);
        assertEquals(testClass, result.getClazz());
        assertEquals(TestResultEnum.FAILED, result.getResult());
        assertEquals(3, result.getTestMethodResults().size());
        assertEquals(2, result.getThrowable().size());
        for (TestMethodResult testMethodResult : result.getTestMethodResults()) {
            assertEquals(TestResultEnum.SKIP, testMethodResult.getResult());
            assertEquals(0, testMethodResult.getThrowable().size());
        }

        inOrder.verify(mockTestClass).beforeAll();
        inOrder.verify(mockTestClass).afterAll();
    }
}