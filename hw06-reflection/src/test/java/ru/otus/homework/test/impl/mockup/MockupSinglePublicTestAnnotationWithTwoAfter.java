package ru.otus.homework.test.impl.mockup;

import ru.otus.homework.test.api.After;
import ru.otus.homework.test.api.Before;
import ru.otus.homework.test.api.Test;

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
