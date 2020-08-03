package ru.otus.homework.herald.core.other;

import ru.otus.homework.herald.api.Log;

import java.util.List;

public class ClassWithLogsMethods2 {

    @Log(comment = "test")
    public void testWithThreeObject(List<? extends Number> numbers, char[] text, Float adjustment) {
    }
}
