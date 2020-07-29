package ru.otus.homework.provoker.impl.mockup;

import ru.otus.homework.provoker.api.After;
import ru.otus.homework.provoker.api.Before;
import ru.otus.homework.provoker.api.Test;

public class MockupSinglePublicTestAnnotationWithAfterAndBefore {

    @After
    public void after() {
    }

    @Before
    public void before() {
    }

    @Test
    public void realTest() {
    }

    public void notRealTest() {
    }
}
