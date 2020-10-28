package ru.otus.homework.hw30;

import java.util.Arrays;
import java.util.List;

public class App {

    private static final List<Integer> TEST_SEQ = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 9, 8, 7, 6, 5, 4, 3, 2, 1);

    public static void main(String[] args) {
        new ByMonitor<Integer>().start(TEST_SEQ);
    }
}
