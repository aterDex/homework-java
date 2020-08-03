package ru.otus.homework.hw08;

import java.lang.ref.SoftReference;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public final class BenchmarkLoaderRef {

    private final int loopCounter;
    private int count = 10000;
    private double flowFactor = 0.08;
    private List<Object[]> flow = new LinkedList<>();
    private SoftReference<byte[]> sr;

    public BenchmarkLoaderRef(int loopCounter) throws Exception {
        this.loopCounter = loopCounter;
    }

    public void run() throws Exception {
        sr = new SoftReference<>(new byte[64 * 1024 * 1024]);
        System.out.println(sr.get().length);
        Random random = new Random(468469);
        int idx = 0;
        int c = 0;
        for (; idx < loopCounter; idx++) {
            printStat(idx, loopCounter);
            Object[] array = createWeight(count);
            Thread.sleep(10);
            if (sr.get() != null) {
                Object[] arc = Arrays.copyOf(array, random.nextInt((int) (count * flowFactor)));
                flow.add(arc);
            } else {
                System.out.println("OutMemory!"); // TODO добавить индекс
                break;
            }
        }
        for (; idx < loopCounter; idx++) {
            printStat(idx, loopCounter);
            c += createWeight(count).length;
            Thread.sleep(10);
        }
    }

    private void printStat(int idx, int loopCounter) {
        if (idx % 1000 == 0) {
            System.out.println("Step idx " + idx + " of " + loopCounter);
        }
    }

    private Object[] createWeight(int count) {
        Object[] array = new Object[count];
        for (int i = 0; i < count; i++) {
            array[i] = new String(new char[0]);
        }
        return array;
    }
}
