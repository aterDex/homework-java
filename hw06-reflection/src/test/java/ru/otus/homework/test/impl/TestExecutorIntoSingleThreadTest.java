package ru.otus.homework.test.impl;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.otus.homework.test.TestClassResult;
import ru.otus.homework.test.TestPrepared;
import ru.otus.homework.test.TestPreparer;
import ru.otus.homework.test.TestResultEnum;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class TestExecutorIntoSingleThreadTest {

    private TestExecutorIntoSingleThread executor;
    private TestPreparer mockPreparer;
    private Class clazz;
    private TestPrepared mockPrepared;
    private Method methodException;

    public static void methodException() {
        throw new RuntimeException();
    }

    @BeforeEach
    void before() throws Exception {
        mockPreparer = mock(TestPreparer.class);
        mockPrepared = mock(TestPrepared.class);
        executor = new TestExecutorIntoSingleThread(mockPreparer);
        clazz = this.getClass();
        when(mockPrepared.getClazz()).thenReturn(clazz);
        when(mockPreparer.prepare(clazz)).thenReturn(mockPrepared);
        methodException = TestExecutorIntoSingleThreadTest.class.getDeclaredMethod("methodException");
    }

    @AfterEach
    void after() throws Exception {
    }

    @Test
    @DisplayName("Проверяем обработку не валидного класса теста")
    void testInvalidPrepared() {
        String problemDescriptionTest = "problems description";
        when(mockPrepared.isValid()).thenReturn(false);
        when(mockPrepared.getProblemsDescription()).thenReturn(problemDescriptionTest);

        TestClassResult result = executor.execute(clazz);
        assertNotNull(result);
        assertEquals(clazz, result.getClazz());
        assertEquals(TestResultEnum.ILLEGAL, result.getResult());
        assertNotNull(result.getTestMethodResults());
        assertEquals(0, result.getTestMethodResults().size());
        assertEquals(problemDescriptionTest, result.getProblemDescription());
    }

    @Test
    @DisplayName("Проверяем обработку если ошибка при BeforeAll")
    void testErrorBeforeAll() throws Exception {
        when(mockPrepared.getBeforeAll()).thenReturn(methodException);
        when(mockPrepared.isValid()).thenReturn(true);

        TestClassResult result = executor.execute(clazz);

        assertNotNull(result);
        assertEquals(clazz, result.getClazz());
        assertEquals(TestResultEnum.FAILED, result.getResult());
        assertNotNull(result.getTestMethodResults());
        assertEquals(0, result.getTestMethodResults().size());
        assertEquals(RuntimeException.class, result.getThrowable().getClass());
    }
}