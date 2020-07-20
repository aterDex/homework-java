package ru.otus.homework.test.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import ru.otus.homework.test.TestClass;
import ru.otus.homework.test.TestClassPreparerException;
import ru.otus.homework.test.impl.mockup.*;

import static org.junit.jupiter.api.Assertions.*;

class TestClassPreparerByAnnotationTest {

    private TestClassPreparerByAnnotation preparer;

    @BeforeEach
    void before() {
        preparer = new TestClassPreparerByAnnotation();
    }

    @Test
    void prepareNullPointerException() {
        assertThrows(NullPointerException.class, () -> preparer.prepare(null));
    }

    @Test
    void prepareSingleTestAnnotation() {
        TestClass testClass = preparer.prepare(MockupSinglePublicTestAnnotation.class);
        assertEquals(1, testClass.getTestMethods().size());
        assertFalse(testClass.isAfterAll());
        assertFalse(testClass.isBeforeAll());
    }

    @Test
    void prepareThreeTestAnnotation() {
        TestClass testClass = preparer.prepare(MockupThreePublicTestAnnotation.class);
        assertEquals(3, testClass.getTestMethods().size());
        assertFalse(testClass.isAfterAll());
        assertFalse(testClass.isBeforeAll());
    }


    @Test
    void prepareAfterAllAndBeforeAll() {
        TestClass testClass = preparer.prepare(MockupSinglePublicTestAnnotationWithAfterAllAndBeforeAll.class);
        assertTrue(testClass.isAfterAll());
        assertTrue(testClass.isBeforeAll());
    }


    @Test
    void prepareAfterAndBefore() {
        TestClass testClass = preparer.prepare(MockupSinglePublicTestAnnotationWithAfterAndBefore.class);
        assertFalse(testClass.isAfterAll());
        assertFalse(testClass.isBeforeAll());
        assertEquals(1, testClass.getTestMethods().size());
        assertTrue(testClass.getTestMethods().get(0).isAfter());
        assertTrue(testClass.getTestMethods().get(0).isBefore());
    }

    @ParameterizedTest
    @ValueSource(classes = {
            MockupEmptyTestAnnotation.class,
            MockupSinglePublicTestAnnotationWithTwoAfter.class,
            MockupSinglePublicTestAnnotationWithTwoAfterAll.class,
            MockupSinglePublicTestAnnotationWithTwoBefore.class,
            MockupSinglePublicTestAnnotationWithTwoBeforeAll.class
    })
    void prepareTestClassPreparerException(Class clazz) {
        assertThrows(TestClassPreparerException.class, () -> preparer.prepare(clazz));
    }
}