package ru.otus.homework.provoker.impl.mockup;

import ru.otus.homework.provoker.api.Test;

public class MockupSinglePublicTestAnnotationWithResult {

    @Test
    public String realTest() {
        return null;
    }

    public void notRealTest() {
    }
}
