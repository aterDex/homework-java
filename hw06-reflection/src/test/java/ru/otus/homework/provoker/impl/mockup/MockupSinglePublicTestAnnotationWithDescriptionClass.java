package ru.otus.homework.provoker.impl.mockup;

import ru.otus.homework.provoker.api.Description;
import ru.otus.homework.provoker.api.Test;

@Description("testClass")
public class MockupSinglePublicTestAnnotationWithDescriptionClass {

    @Test
    public void realTest() {
    }

    public void notRealTest() {
    }
}
