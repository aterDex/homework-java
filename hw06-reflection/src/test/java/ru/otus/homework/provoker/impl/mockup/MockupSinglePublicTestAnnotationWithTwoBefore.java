package ru.otus.homework.provoker.impl.mockup;

import ru.otus.homework.provoker.api.Before;
import ru.otus.homework.provoker.api.Test;

public class MockupSinglePublicTestAnnotationWithTwoBefore {

    @Before
    public void before0() {
    }

    @Before
    public void before1() {
    }

    @Test
    public void realTest() {
    }

    public void notRealTest() {
    }
}
