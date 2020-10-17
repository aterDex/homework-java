package ru.otus.homework.hw08;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class Benchmark {

    private final int loopCounter;
    private final int size = 10000;
    private final int flowSize = 400;
    private final List<Object[]> flow = new LinkedList<>();

    public Benchmark(int loopCounter) {
        this.loopCounter = loopCounter;
    }

    void run() throws InterruptedException {
        for (int idx = 0; idx < loopCounter; idx++) {
            String[] array = new String[size];
            for (int i = 0; i < size; i++) {
                array[i] = new String(new char[0]);
            }
            flow.add(Arrays.copyOf(array, flowSize));
            Thread.sleep(10);
        }
    }
}
