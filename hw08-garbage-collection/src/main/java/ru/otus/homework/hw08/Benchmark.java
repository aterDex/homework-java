package ru.otus.homework.hw08;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class Benchmark {

    private final int loopCounter;
    private int size = 10000;
    private double flowFactor = 0.08;
    private List<Object[]> flow = new LinkedList<>();

    public Benchmark(int loopCounter) {
        this.loopCounter = loopCounter;
    }

    void run() throws InterruptedException {
        Random random = new Random(468469);
        for (int idx = 0; idx < loopCounter; idx++) {
            int local = size;
            String[] array = new String[local];
            for (int i = 0; i < local; i++) {
                array[i] = new String(new char[0]);
            }
            String[] arc = Arrays.copyOf(array, random.nextInt((int) (size * flowFactor)));
            flow.add(arc);
            Thread.sleep(10);
            System.out.println(Runtime.getRuntime().freeMemory());
        }
    }
}
