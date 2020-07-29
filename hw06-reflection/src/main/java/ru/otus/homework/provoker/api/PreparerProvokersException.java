package ru.otus.homework.provoker.api;

/**
 * Класс с тестом не возможно обработать...
 */
public class PreparerProvokersException extends RuntimeException {

    private final Class<?> clazz;

    public PreparerProvokersException(String message, Class<?> clazz) {
        super(message);
        this.clazz = clazz;
    }

    /**
     * @return Класс с тестом
     */
    public Class<?> getClazz() {
        return clazz;
    }
}
