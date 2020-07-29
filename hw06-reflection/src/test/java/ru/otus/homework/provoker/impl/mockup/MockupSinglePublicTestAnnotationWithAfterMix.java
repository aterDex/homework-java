package ru.otus.homework.provoker.impl.mockup;

import ru.otus.homework.provoker.api.After;
import ru.otus.homework.provoker.api.Test;

public class MockupSinglePublicTestAnnotationWithAfterMix {

    @After
    @Test
    public void realTest() {
    }

    public void notRealTest() {
    }
}
