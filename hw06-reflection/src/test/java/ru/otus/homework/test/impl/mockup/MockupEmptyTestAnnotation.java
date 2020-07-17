package ru.otus.homework.test.impl.mockup;

import ru.otus.homework.test.api.After;
import ru.otus.homework.test.api.AfterAll;
import ru.otus.homework.test.api.Before;
import ru.otus.homework.test.api.BeforeAll;

public class MockupEmptyTestAnnotation {

    @AfterAll
    public void afterAll() {
    }

    @BeforeAll
    public void beforeAll() {
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
