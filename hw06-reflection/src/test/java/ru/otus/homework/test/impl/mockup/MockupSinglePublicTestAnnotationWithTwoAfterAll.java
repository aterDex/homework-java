package ru.otus.homework.test.impl.mockup;

import ru.otus.homework.test.api.After;
import ru.otus.homework.test.api.AfterAll;
import ru.otus.homework.test.api.Test;

public class MockupSinglePublicTestAnnotationWithTwoAfterAll {

    @AfterAll
    public void after0() {
    }

    @AfterAll
    public void after1() {
    }

    @Test
    public void realTest() {
    }

    public void notRealTest() {
    }
}
