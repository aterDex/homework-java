package ru.otus.homework.test.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import ru.otus.homework.test.TestPrepared;
import ru.otus.homework.test.impl.mockup.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class TestPreparerByAnnotationTest {

    private TestPreparerByAnnotation preparer;

    @BeforeEach
    void before() {
        preparer = new TestPreparerByAnnotation();
    }

    @Test
    void testSingleTestAnnotation() {
        Class mockup = MockupSinglePublicTestAnnotation.class;
        TestPrepared prepare = preparer.prepare(mockup);
        assertTrue(prepare.isValid());
        assertEquals(mockup, prepare.getClazz());
        assertNull(prepare.getAfter());
        assertNull(prepare.getAfterAll());
        assertNull(prepare.getBefore());
        assertNull(prepare.getBeforeAll());
        assertNull(prepare.getProblemsDescription());
        assertNotNull(prepare.getTests());
        assertEquals(1, prepare.getTests().size());
        assertEquals("realTest", prepare.getTests().iterator().next().getName());
    }

    @Test
    void testThreeTestAnnotation() {
        Class mockup = MockupThreePublicTestAnnotation.class;
        TestPrepared prepare = preparer.prepare(mockup);
        assertTrue(prepare.isValid());
        assertEquals(mockup, prepare.getClazz());
        assertNull(prepare.getAfter());
        assertNull(prepare.getAfterAll());
        assertNull(prepare.getBefore());
        assertNull(prepare.getBeforeAll());
        assertNull(prepare.getProblemsDescription());
        assertNotNull(prepare.getTests());
        assertEquals(3, prepare.getTests().size());

        List<String> namesTest = prepare.getTests().stream().map(x -> x.getName()).collect(Collectors.toList());
        List<String> expectedMethodsName = Arrays.asList("realTest0", "realTest1", "realTest2");
        assertEquals(expectedMethodsName.size(), namesTest.size());
        assertTrue(expectedMethodsName.containsAll(namesTest));
        assertTrue(namesTest.containsAll(expectedMethodsName));
    }

    @Test
    void testAfterAndBeforeAnnotation() {
        Class mockup = MockupSinglePublicTestAnnotationWithAfterAndBefore.class;
        TestPrepared prepare = preparer.prepare(mockup);
        assertTrue(prepare.isValid());
        assertEquals(mockup, prepare.getClazz());
        assertNotNull(prepare.getAfter());
        assertEquals("after", prepare.getAfter().getName());
        assertNotNull(prepare.getBefore());
        assertEquals("before", prepare.getBefore().getName());
        assertNull(prepare.getAfterAll());
        assertNull(prepare.getBeforeAll());
        assertNull(prepare.getProblemsDescription());
        assertNotNull(prepare.getTests());
    }

    @Test
    void testAfterAllAndBeforeAllAnnotation() {
        Class mockup = MockupSinglePublicTestAnnotationWithAfterAllAndBeforeAll.class;
        TestPrepared prepare = preparer.prepare(mockup);
        assertTrue(prepare.isValid());
        assertEquals(mockup, prepare.getClazz());
        assertNull(prepare.getAfter());
        assertNull(prepare.getBefore());
        assertNotNull(prepare.getAfterAll());
        assertEquals("afterAll", prepare.getAfterAll().getName());
        assertNotNull(prepare.getBeforeAll());
        assertEquals("beforeAll", prepare.getBeforeAll().getName());
        assertNull(prepare.getProblemsDescription());
        assertNotNull(prepare.getTests());
    }

    @DisplayName("Проверяем классы тестов оформленных не верно")
    @ParameterizedTest
    @ValueSource(classes = {
            MockupSinglePublicTestAnnotationWithTwoAfter.class,
            MockupSinglePublicTestAnnotationWithTwoBefore.class,
            MockupSinglePublicTestAnnotationWithTwoAfterAll.class,
            MockupSinglePublicTestAnnotationWithTwoBeforeAll.class,
            MockupEmptyTestAnnotation.class})
    void testProblem(Class mockup) {
        TestPrepared prepare = preparer.prepare(mockup);
        assertFalse(prepare.isValid());
        assertNotNull(prepare.getProblemsDescription());
        assertNull(prepare.getAfter());
        assertNull(prepare.getBefore());
        assertNull(prepare.getAfterAll());
        assertNull(prepare.getBeforeAll());
        assertNull(prepare.getTests());
    }
}