package ru.otus.homework.test.impl.mockup;

import ru.otus.homework.test.api.After;
import ru.otus.homework.test.api.Before;
import ru.otus.homework.test.api.Test;

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
