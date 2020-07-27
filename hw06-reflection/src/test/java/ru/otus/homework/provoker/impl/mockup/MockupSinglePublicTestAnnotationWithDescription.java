package ru.otus.homework.provoker.impl.mockup;

import ru.otus.homework.provoker.api.Description;
import ru.otus.homework.provoker.api.Test;

public class MockupSinglePublicTestAnnotationWithDescription {

    @Description("test")
    @Test
    public void realTest() {
    }

    public void notRealTest() {
    }
}
