package ru.otus.homework.test;

/**
 * Класс с тестом не возможно обработать...
 */
public class TestClassPreparerException extends RuntimeException {

    private final Class clazz;

    public TestClassPreparerException(String message, Class clazz) {
        super(message);
        this.clazz = clazz;
    }

    /**
     * @return Класс с тестом
     */
    public Class getClazz() {
        return clazz;
    }
}
