package ru.otus.homework.provoker.impl.mockup;

import ru.otus.homework.provoker.api.Test;

public class MockupSinglePublicTestAnnotationWithArg {

    @Test
    public void realTest(String test) {
    }

    public void notRealTest() {
    }
}
