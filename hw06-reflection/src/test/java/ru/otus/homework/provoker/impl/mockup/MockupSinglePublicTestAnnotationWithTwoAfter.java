package ru.otus.homework.provoker.impl.mockup;

import ru.otus.homework.provoker.api.After;
import ru.otus.homework.provoker.api.Test;

public class MockupSinglePublicTestAnnotationWithTwoAfter {

    @After
    public void after0() {
    }

    @After
    public void after1() {
    }

    @Test
    public void realTest() {
    }

    public void notRealTest() {
    }
}
