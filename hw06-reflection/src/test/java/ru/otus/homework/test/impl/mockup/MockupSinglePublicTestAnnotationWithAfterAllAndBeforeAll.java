package ru.otus.homework.test.impl.mockup;

import ru.otus.homework.test.api.AfterAll;
import ru.otus.homework.test.api.BeforeAll;
import ru.otus.homework.test.api.Test;

public class MockupSinglePublicTestAnnotationWithAfterAllAndBeforeAll {

    @AfterAll
    public void afterAll() {
    }

    @BeforeAll
    public void beforeAll() {
    }

    @Test
    public void realTest() {
    }

    public void notRealTest() {
    }
}
