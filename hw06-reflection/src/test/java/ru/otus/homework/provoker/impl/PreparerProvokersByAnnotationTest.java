package ru.otus.homework.provoker.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import ru.otus.homework.provoker.ProvokerClass;
import ru.otus.homework.provoker.PreparerProvokersException;
import ru.otus.homework.provoker.impl.mockup.*;

import static org.junit.jupiter.api.Assertions.*;

class PreparerProvokersByAnnotationTest {

    private PreparerProvokersByAnnotation preparer;

    @BeforeEach
    void before() {
        preparer = new PreparerProvokersByAnnotation();
    }

    @Test
    void prepareNullPointerException() {
        assertThrows(NullPointerException.class, () -> preparer.prepare(null));
    }

    @Test
    void prepareSingleTestAnnotation() {
        ProvokerClass provokerClass = preparer.prepare(MockupSinglePublicTestAnnotation.class);
        assertEquals(1, provokerClass.getTestMethods().size());
        assertFalse(provokerClass.isAfterAll());
        assertFalse(provokerClass.isBeforeAll());
    }

    @Test
    void prepareThreeTestAnnotation() {
        ProvokerClass provokerClass = preparer.prepare(MockupThreePublicTestAnnotation.class);
        assertEquals(3, provokerClass.getTestMethods().size());
        assertFalse(provokerClass.isAfterAll());
        assertFalse(provokerClass.isBeforeAll());
    }


    @Test
    void prepareAfterAllAndBeforeAll() {
        ProvokerClass provokerClass = preparer.prepare(MockupSinglePublicTestAnnotationWithAfterAllAndBeforeAll.class);
        assertTrue(provokerClass.isAfterAll());
        assertTrue(provokerClass.isBeforeAll());
    }


    @Test
    void prepareAfterAndBefore() {
        ProvokerClass provokerClass = preparer.prepare(MockupSinglePublicTestAnnotationWithAfterAndBefore.class);
        assertFalse(provokerClass.isAfterAll());
        assertFalse(provokerClass.isBeforeAll());
        assertEquals(1, provokerClass.getTestMethods().size());
        assertTrue(provokerClass.getTestMethods().get(0).isAfter());
        assertTrue(provokerClass.getTestMethods().get(0).isBefore());
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
        assertThrows(PreparerProvokersException.class, () -> preparer.prepare(clazz));
    }
}