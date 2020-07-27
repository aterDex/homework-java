package ru.otus.homework.provoker.impl.mockup;

import ru.otus.homework.provoker.api.BeforeAll;
import ru.otus.homework.provoker.api.Test;

public class MockupSinglePublicTestAnnotationWithBeforeAllNotStatic {

    @BeforeAll
    public void before0() {
    }

    @Test
    public void realTest() {
    }

    public void notRealTest() {
    }
}
