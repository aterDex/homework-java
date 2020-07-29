package ru.otus.homework.hw06.test;

import ru.otus.homework.provoker.api.Test;

public class Test02 {

    @Test
    public void emptyTest0() {
    }

    @Test
    public void exceptionTest1() {
        throw new RuntimeException("I a'm exceptionTest1 and I throw exception!");
    }

    @Test
    public void emptyTest2() {
    }
}
