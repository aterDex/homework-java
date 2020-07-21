package ru.otus.homework.provoker.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.otus.homework.provoker.*;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ExecutorIntoSingleThreadTest {

    private ExecutorIntoSingleThread executor;
    @Mock
    private PreparerProvokers mockPreparer;
    @Mock
    private ProvokerClass mockProvokerClass;
    @Mock
    private ProvokerClassMethod mockProvokerClassMethod0;
    @Mock
    private ProvokerClassMethod mockProvokerClassMethod1;
    @Mock
    private ProvokerClassMethod mockProvokerClassMethod2;

    private Class testClass;

    @BeforeEach
    void before() {
        testClass = ExecutorIntoSingleThreadTest.class;
        executor = new ExecutorIntoSingleThread(mockPreparer);
    }

    @ParameterizedTest
    @ValueSource(classes = {
            PreparerProvokersException.class,
            NullPointerException.class
    })
    void executeException(Class exception) {
        when(mockPreparer.prepare(testClass)).thenThrow(exception);
        ProvokerClassResult result = executor.execute(testClass);
        assertNotNull(result);
        assertEquals(1, result.getThrowable().size());
        assertEquals(0, result.getTestMethodResults().size());
        assertEquals(ResultEnum.ILLEGAL, result.getResult());
        assertEquals(testClass, result.getClazz());
        assertEquals(exception, result.getThrowable().get(0).getClass());
    }

    @Test
    void executeAllMethodOk() {
        when(mockPreparer.prepare(testClass)).thenReturn(mockProvokerClass);
        when(mockProvokerClass.getTestMethods()).thenReturn(List.of(mockProvokerClassMethod0, mockProvokerClassMethod1, mockProvokerClassMethod2));
        when(mockProvokerClass.getClazz()).thenReturn(testClass);

        InOrder inOrder = inOrder(mockProvokerClass, mockProvokerClassMethod0, mockProvokerClassMethod1, mockProvokerClassMethod2);

        ProvokerClassResult result = executor.execute(testClass);
        assertNotNull(result);
        assertEquals(testClass, result.getClazz());
        assertEquals(ResultEnum.OK, result.getResult());
        assertEquals(3, result.getTestMethodResults().size());
        assertEquals(0, result.getThrowable().size());
        for (ProvokerClassMethodResult testMethodResult : result.getTestMethodResults()) {
            assertEquals(ResultEnum.OK, testMethodResult.getResult());
            assertEquals(0, testMethodResult.getThrowable().size());
        }

        inOrder.verify(mockProvokerClass).beforeAll();
        inOrder.verify(mockProvokerClassMethod0).init();
        inOrder.verify(mockProvokerClassMethod0).before();
        inOrder.verify(mockProvokerClassMethod0).test();
        inOrder.verify(mockProvokerClassMethod0).after();
        inOrder.verify(mockProvokerClassMethod1).init();
        inOrder.verify(mockProvokerClassMethod1).before();
        inOrder.verify(mockProvokerClassMethod1).test();
        inOrder.verify(mockProvokerClassMethod1).after();
        inOrder.verify(mockProvokerClassMethod2).init();
        inOrder.verify(mockProvokerClassMethod2).before();
        inOrder.verify(mockProvokerClassMethod2).test();
        inOrder.verify(mockProvokerClassMethod2).after();
        inOrder.verify(mockProvokerClass).afterAll();
    }

    @Test
    void executeMethodTestFailed() {
        String exceptionMethodName = "ExceptionTest";

        NullPointerException testException = new NullPointerException();
        when(mockPreparer.prepare(testClass)).thenReturn(mockProvokerClass);
        when(mockProvokerClass.getTestMethods()).thenReturn(List.of(mockProvokerClassMethod0, mockProvokerClassMethod1, mockProvokerClassMethod2));
        when(mockProvokerClassMethod1.test()).thenReturn(Optional.of(testException));
        when(mockProvokerClassMethod1.getMethodName()).thenReturn(exceptionMethodName);
        when(mockProvokerClass.getClazz()).thenReturn(testClass);

        InOrder inOrder = inOrder(mockProvokerClass, mockProvokerClassMethod0, mockProvokerClassMethod1, mockProvokerClassMethod2);

        ProvokerClassResult result = executor.execute(testClass);
        assertNotNull(result);
        assertEquals(testClass, result.getClazz());
        assertEquals(ResultEnum.FAILED, result.getResult());
        assertEquals(3, result.getTestMethodResults().size());
        assertEquals(0, result.getThrowable().size());
        for (ProvokerClassMethodResult testMethodResult : result.getTestMethodResults()) {
            if (exceptionMethodName.equals(testMethodResult.getMethodName())) {
                assertEquals(ResultEnum.FAILED, testMethodResult.getResult());
                assertEquals(1, testMethodResult.getThrowable().size());
            } else {
                assertEquals(ResultEnum.OK, testMethodResult.getResult());
                assertEquals(0, testMethodResult.getThrowable().size());
            }
        }

        inOrder.verify(mockProvokerClass).beforeAll();
        inOrder.verify(mockProvokerClassMethod0).init();
        inOrder.verify(mockProvokerClassMethod0).before();
        inOrder.verify(mockProvokerClassMethod0).test();
        inOrder.verify(mockProvokerClassMethod0).after();
        inOrder.verify(mockProvokerClassMethod1).init();
        inOrder.verify(mockProvokerClassMethod1).before();
        inOrder.verify(mockProvokerClassMethod1).test();
        inOrder.verify(mockProvokerClassMethod1).after();
        inOrder.verify(mockProvokerClassMethod2).init();
        inOrder.verify(mockProvokerClassMethod2).before();
        inOrder.verify(mockProvokerClassMethod2).test();
        inOrder.verify(mockProvokerClassMethod2).after();
        inOrder.verify(mockProvokerClass).afterAll();
    }

    @Test
    void executeMethodBeforeTestFailed() {
        String exceptionMethodName = "ExceptionTest";

        NullPointerException testException = new NullPointerException();
        when(mockPreparer.prepare(testClass)).thenReturn(mockProvokerClass);
        when(mockProvokerClass.getTestMethods()).thenReturn(List.of(mockProvokerClassMethod0, mockProvokerClassMethod1, mockProvokerClassMethod2));
        when(mockProvokerClassMethod1.before()).thenReturn(Optional.of(testException));
        when(mockProvokerClassMethod1.getMethodName()).thenReturn(exceptionMethodName);
        when(mockProvokerClass.getClazz()).thenReturn(testClass);

        InOrder inOrder = inOrder(mockProvokerClass, mockProvokerClassMethod0, mockProvokerClassMethod1, mockProvokerClassMethod2);

        ProvokerClassResult result = executor.execute(testClass);
        assertNotNull(result);
        assertEquals(testClass, result.getClazz());
        assertEquals(ResultEnum.FAILED, result.getResult());
        assertEquals(3, result.getTestMethodResults().size());
        assertEquals(0, result.getThrowable().size());
        for (ProvokerClassMethodResult testMethodResult : result.getTestMethodResults()) {
            if (exceptionMethodName.equals(testMethodResult.getMethodName())) {
                assertEquals(ResultEnum.FAILED, testMethodResult.getResult());
                assertEquals(1, testMethodResult.getThrowable().size());
            } else {
                assertEquals(ResultEnum.OK, testMethodResult.getResult());
                assertEquals(0, testMethodResult.getThrowable().size());
            }
        }

        inOrder.verify(mockProvokerClass).beforeAll();
        inOrder.verify(mockProvokerClassMethod0).init();
        inOrder.verify(mockProvokerClassMethod0).before();
        inOrder.verify(mockProvokerClassMethod0).test();
        inOrder.verify(mockProvokerClassMethod0).after();
        inOrder.verify(mockProvokerClassMethod1).init();
        inOrder.verify(mockProvokerClassMethod1).before();

        inOrder.verify(mockProvokerClassMethod1).after();
        inOrder.verify(mockProvokerClassMethod2).init();
        inOrder.verify(mockProvokerClassMethod2).before();
        inOrder.verify(mockProvokerClassMethod2).test();
        inOrder.verify(mockProvokerClassMethod2).after();
        inOrder.verify(mockProvokerClass).afterAll();
    }

    @Test
    void executeMethodAfterTestFailed() {
        String exceptionMethodName = "ExceptionTest";

        NullPointerException testException = new NullPointerException();
        when(mockPreparer.prepare(testClass)).thenReturn(mockProvokerClass);
        when(mockProvokerClass.getTestMethods()).thenReturn(List.of(mockProvokerClassMethod0, mockProvokerClassMethod1, mockProvokerClassMethod2));
        when(mockProvokerClassMethod1.after()).thenReturn(Optional.of(testException));
        when(mockProvokerClassMethod1.getMethodName()).thenReturn(exceptionMethodName);
        when(mockProvokerClass.getClazz()).thenReturn(testClass);

        InOrder inOrder = inOrder(mockProvokerClass, mockProvokerClassMethod0, mockProvokerClassMethod1, mockProvokerClassMethod2);

        ProvokerClassResult result = executor.execute(testClass);
        assertNotNull(result);
        assertEquals(testClass, result.getClazz());
        assertEquals(ResultEnum.FAILED, result.getResult());
        assertEquals(3, result.getTestMethodResults().size());
        assertEquals(0, result.getThrowable().size());
        for (ProvokerClassMethodResult testMethodResult : result.getTestMethodResults()) {
            if (exceptionMethodName.equals(testMethodResult.getMethodName())) {
                assertEquals(ResultEnum.FAILED, testMethodResult.getResult());
                assertEquals(1, testMethodResult.getThrowable().size());
            } else {
                assertEquals(ResultEnum.OK, testMethodResult.getResult());
                assertEquals(0, testMethodResult.getThrowable().size());
            }
        }

        inOrder.verify(mockProvokerClass).beforeAll();
        inOrder.verify(mockProvokerClassMethod0).init();
        inOrder.verify(mockProvokerClassMethod0).before();
        inOrder.verify(mockProvokerClassMethod0).test();
        inOrder.verify(mockProvokerClassMethod0).after();
        inOrder.verify(mockProvokerClassMethod1).init();
        inOrder.verify(mockProvokerClassMethod1).before();
        inOrder.verify(mockProvokerClassMethod1).test();
        inOrder.verify(mockProvokerClassMethod1).after();
        inOrder.verify(mockProvokerClassMethod2).init();
        inOrder.verify(mockProvokerClassMethod2).before();
        inOrder.verify(mockProvokerClassMethod2).test();
        inOrder.verify(mockProvokerClassMethod2).after();
        inOrder.verify(mockProvokerClass).afterAll();
    }

    @Test
    void executeMethodBeforeAndAfterTestFailedAndTestSkip() {
        String exceptionMethodName = "ExceptionTest";

        NullPointerException testException = new NullPointerException();
        when(mockPreparer.prepare(testClass)).thenReturn(mockProvokerClass);
        when(mockProvokerClass.getTestMethods()).thenReturn(List.of(mockProvokerClassMethod0, mockProvokerClassMethod1, mockProvokerClassMethod2));
        when(mockProvokerClassMethod1.before()).thenReturn(Optional.of(testException));
        when(mockProvokerClassMethod1.after()).thenReturn(Optional.of(testException));
        when(mockProvokerClassMethod1.getMethodName()).thenReturn(exceptionMethodName);
        when(mockProvokerClass.getClazz()).thenReturn(testClass);

        InOrder inOrder = inOrder(mockProvokerClass, mockProvokerClassMethod0, mockProvokerClassMethod1, mockProvokerClassMethod2);

        ProvokerClassResult result = executor.execute(testClass);
        assertNotNull(result);
        assertEquals(testClass, result.getClazz());
        assertEquals(ResultEnum.FAILED, result.getResult());
        assertEquals(3, result.getTestMethodResults().size());
        assertEquals(0, result.getThrowable().size());
        for (ProvokerClassMethodResult testMethodResult : result.getTestMethodResults()) {
            if (exceptionMethodName.equals(testMethodResult.getMethodName())) {
                assertEquals(ResultEnum.FAILED, testMethodResult.getResult());
                assertEquals(2, testMethodResult.getThrowable().size());
            } else {
                assertEquals(ResultEnum.OK, testMethodResult.getResult());
                assertEquals(0, testMethodResult.getThrowable().size());
            }
        }

        inOrder.verify(mockProvokerClass).beforeAll();
        inOrder.verify(mockProvokerClassMethod0).init();
        inOrder.verify(mockProvokerClassMethod0).before();
        inOrder.verify(mockProvokerClassMethod0).test();
        inOrder.verify(mockProvokerClassMethod0).after();
        inOrder.verify(mockProvokerClassMethod1).init();
        inOrder.verify(mockProvokerClassMethod1).before();

        inOrder.verify(mockProvokerClassMethod1).after();
        inOrder.verify(mockProvokerClassMethod2).init();
        inOrder.verify(mockProvokerClassMethod2).before();
        inOrder.verify(mockProvokerClassMethod2).test();
        inOrder.verify(mockProvokerClassMethod2).after();
        inOrder.verify(mockProvokerClass).afterAll();
    }

    @Test
    void executeMethodInitTestFailed() {
        String exceptionMethodName = "ExceptionTest";

        NullPointerException testException = new NullPointerException();
        when(mockPreparer.prepare(testClass)).thenReturn(mockProvokerClass);
        when(mockProvokerClass.getTestMethods()).thenReturn(List.of(mockProvokerClassMethod0, mockProvokerClassMethod1, mockProvokerClassMethod2));
        when(mockProvokerClassMethod1.init()).thenReturn(Optional.of(testException));
        when(mockProvokerClassMethod1.getMethodName()).thenReturn(exceptionMethodName);
        when(mockProvokerClass.getClazz()).thenReturn(testClass);

        InOrder inOrder = inOrder(mockProvokerClass, mockProvokerClassMethod0, mockProvokerClassMethod1, mockProvokerClassMethod2);

        ProvokerClassResult result = executor.execute(testClass);
        assertNotNull(result);
        assertEquals(testClass, result.getClazz());
        assertEquals(ResultEnum.FAILED, result.getResult());
        assertEquals(3, result.getTestMethodResults().size());
        assertEquals(0, result.getThrowable().size());
        for (ProvokerClassMethodResult testMethodResult : result.getTestMethodResults()) {
            if (exceptionMethodName.equals(testMethodResult.getMethodName())) {
                assertEquals(ResultEnum.FAILED, testMethodResult.getResult());
                assertEquals(1, testMethodResult.getThrowable().size());
            } else {
                assertEquals(ResultEnum.OK, testMethodResult.getResult());
                assertEquals(0, testMethodResult.getThrowable().size());
            }
        }

        inOrder.verify(mockProvokerClass).beforeAll();
        inOrder.verify(mockProvokerClassMethod0).init();
        inOrder.verify(mockProvokerClassMethod0).before();
        inOrder.verify(mockProvokerClassMethod0).test();
        inOrder.verify(mockProvokerClassMethod0).after();
        inOrder.verify(mockProvokerClassMethod1).init();
        inOrder.verify(mockProvokerClassMethod2).init();
        inOrder.verify(mockProvokerClassMethod2).before();
        inOrder.verify(mockProvokerClassMethod2).test();
        inOrder.verify(mockProvokerClassMethod2).after();
        inOrder.verify(mockProvokerClass).afterAll();
    }

    @Test
    void executeBeforeAllException() {
        NullPointerException testException = new NullPointerException();

        when(mockPreparer.prepare(testClass)).thenReturn(mockProvokerClass);
        when(mockProvokerClass.getTestMethods()).thenReturn(List.of(mockProvokerClassMethod0, mockProvokerClassMethod1, mockProvokerClassMethod2));
        when(mockProvokerClass.beforeAll()).thenReturn(Optional.of(testException));
        when(mockProvokerClass.getClazz()).thenReturn(testClass);

        InOrder inOrder = inOrder(mockProvokerClass, mockProvokerClassMethod0, mockProvokerClassMethod1, mockProvokerClassMethod2);

        ProvokerClassResult result = executor.execute(testClass);
        assertNotNull(result);
        assertEquals(testClass, result.getClazz());
        assertEquals(ResultEnum.FAILED, result.getResult());
        assertEquals(3, result.getTestMethodResults().size());
        assertEquals(1, result.getThrowable().size());
        for (ProvokerClassMethodResult testMethodResult : result.getTestMethodResults()) {
            assertEquals(ResultEnum.SKIP, testMethodResult.getResult());
            assertEquals(0, testMethodResult.getThrowable().size());
        }

        inOrder.verify(mockProvokerClass).beforeAll();
        inOrder.verify(mockProvokerClass).afterAll();
    }

    @Test
    void executeAfterAllException() {
        NullPointerException testException = new NullPointerException();

        when(mockPreparer.prepare(testClass)).thenReturn(mockProvokerClass);
        when(mockProvokerClass.getTestMethods()).thenReturn(List.of(mockProvokerClassMethod0, mockProvokerClassMethod1, mockProvokerClassMethod2));
        when(mockProvokerClass.afterAll()).thenReturn(Optional.of(testException));
        when(mockProvokerClass.getClazz()).thenReturn(testClass);

        InOrder inOrder = inOrder(mockProvokerClass, mockProvokerClassMethod0, mockProvokerClassMethod1, mockProvokerClassMethod2);

        ProvokerClassResult result = executor.execute(testClass);
        assertNotNull(result);
        assertEquals(testClass, result.getClazz());
        assertEquals(ResultEnum.FAILED, result.getResult());
        assertEquals(3, result.getTestMethodResults().size());
        assertEquals(1, result.getThrowable().size());
        for (ProvokerClassMethodResult testMethodResult : result.getTestMethodResults()) {
            assertEquals(ResultEnum.OK, testMethodResult.getResult());
            assertEquals(0, testMethodResult.getThrowable().size());
        }

        inOrder.verify(mockProvokerClass).beforeAll();
        inOrder.verify(mockProvokerClassMethod0).init();
        inOrder.verify(mockProvokerClassMethod0).before();
        inOrder.verify(mockProvokerClassMethod0).test();
        inOrder.verify(mockProvokerClassMethod0).after();
        inOrder.verify(mockProvokerClassMethod1).init();
        inOrder.verify(mockProvokerClassMethod1).before();
        inOrder.verify(mockProvokerClassMethod1).test();
        inOrder.verify(mockProvokerClassMethod1).after();
        inOrder.verify(mockProvokerClassMethod2).init();
        inOrder.verify(mockProvokerClassMethod2).before();
        inOrder.verify(mockProvokerClassMethod2).test();
        inOrder.verify(mockProvokerClassMethod2).after();
        inOrder.verify(mockProvokerClass).afterAll();
    }

    @Test
    void executeBeforeAllAndAfterAllException() {
        NullPointerException testException = new NullPointerException();

        when(mockPreparer.prepare(testClass)).thenReturn(mockProvokerClass);
        when(mockProvokerClass.getTestMethods()).thenReturn(List.of(mockProvokerClassMethod0, mockProvokerClassMethod1, mockProvokerClassMethod2));
        when(mockProvokerClass.afterAll()).thenReturn(Optional.of(testException));
        when(mockProvokerClass.beforeAll()).thenReturn(Optional.of(testException));
        when(mockProvokerClass.getClazz()).thenReturn(testClass);

        InOrder inOrder = inOrder(mockProvokerClass, mockProvokerClassMethod0, mockProvokerClassMethod1, mockProvokerClassMethod2);

        ProvokerClassResult result = executor.execute(testClass);
        assertNotNull(result);
        assertEquals(testClass, result.getClazz());
        assertEquals(ResultEnum.FAILED, result.getResult());
        assertEquals(3, result.getTestMethodResults().size());
        assertEquals(2, result.getThrowable().size());
        for (ProvokerClassMethodResult testMethodResult : result.getTestMethodResults()) {
            assertEquals(ResultEnum.SKIP, testMethodResult.getResult());
            assertEquals(0, testMethodResult.getThrowable().size());
        }

        inOrder.verify(mockProvokerClass).beforeAll();
        inOrder.verify(mockProvokerClass).afterAll();
    }
}