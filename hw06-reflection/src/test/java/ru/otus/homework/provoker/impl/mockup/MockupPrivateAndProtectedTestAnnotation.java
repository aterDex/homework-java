package ru.otus.homework.provoker.impl.mockup;

import ru.otus.homework.provoker.api.Test;

public class MockupPrivateAndProtectedTestAnnotation {

    @Test
    private void realTestPrivate() {
        System.out.println("Private");
    }

    @Test
    protected void realTestProtected() {
        System.out.println("Protected");
    }

    @Test
    void realTestPackProtected() {
        System.out.println("PackProtected");
    }
}
