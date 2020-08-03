package ru.otus.homework.herald.core.other;

public class ClassLoaderForTest extends ClassLoader {

    public Class<?> defineClass(String className, byte[] b) {
        return this.defineClass(className, b, 0, b.length);
    }
}
