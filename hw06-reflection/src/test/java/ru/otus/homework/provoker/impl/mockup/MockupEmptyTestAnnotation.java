package ru.otus.homework.provoker.impl.mockup;

import ru.otus.homework.provoker.api.After;
import ru.otus.homework.provoker.api.AfterAll;
import ru.otus.homework.provoker.api.Before;
import ru.otus.homework.provoker.api.BeforeAll;

public class MockupEmptyTestAnnotation {

    @AfterAll
    public static void afterAll() {
    }

    @BeforeAll
    public static void beforeAll() {
    }

    @After
    public void after() {
    }

    @Before
    public void before() {
    }

    public void notRealTest() {
    }
}
