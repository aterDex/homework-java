package ru.otus.homework.provoker.impl.mockup;

import ru.otus.homework.provoker.api.AfterAll;
import ru.otus.homework.provoker.api.Test;

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
