package ru.otus.homework.provoker;

public interface Executor {

    /**
     * Выполняем все тесты в классе и возвращаем результат
     *
     * @param aClass класс с тестами
     * @return Результаты тестов
     */
    ProvokerClassResult execute(Class aClass);
}
