package ru.otus.homework.provoker.impl.mockup;

import ru.otus.homework.provoker.api.AfterAll;
import ru.otus.homework.provoker.api.BeforeAll;
import ru.otus.homework.provoker.api.Test;

public class MockupSinglePublicTestAnnotationWithAfterAllAndBeforeAll {

    @AfterAll
    public static void afterAll() {
    }

    @BeforeAll
    public static void beforeAll() {
    }

    @Test
    public void realTest() {
    }

    public void notRealTest() {
    }
}
