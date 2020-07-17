package ru.otus.homework.test.impl.mockup;

import ru.otus.homework.test.api.Before;
import ru.otus.homework.test.api.Test;

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
