package ru.otus.homework.test.impl.mockup;

import ru.otus.homework.test.api.Before;
import ru.otus.homework.test.api.BeforeAll;
import ru.otus.homework.test.api.Test;

public class MockupSinglePublicTestAnnotationWithTwoBeforeAll {

    @BeforeAll
    public void before0() {
    }

    @BeforeAll
    public void before1() {
    }

    @Test
    public void realTest() {
    }

    public void notRealTest() {
    }
}
