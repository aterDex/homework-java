package ru.otus.homework.hw08;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public final class BenchmarkLoader {

    private static final byte[] FILLER = "Lorem ipsum dolor sit amet".getBytes(StandardCharsets.UTF_8);
    private final int loopCounter;
//    private final Cipher c;
    private int count = 10000;
    private double flowFactor = 0.08;
    private List<Object[]> flow = new LinkedList<>();
    private byte[] fillerLocal = Arrays.copyOf(FILLER, FILLER.length);

    public BenchmarkLoader(int loopCounter) throws Exception {
        this.loopCounter = loopCounter;
//        this.c = Cipher.getInstance("AES/CBC/PKCS5Padding");
//        this.c.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(new byte[]{'0', '2', '3', '4', '5', '6', '7', '8', '9', '1', '2', '3', '4', '5', '6', '7'}, "AES"));
    }

    public void run() throws Exception {
        Random random = new Random(468469);
        final long totalMemory = Runtime.getRuntime().totalMemory();
        final long totalFreeMemory = Runtime.getRuntime().freeMemory();
        final long limitMemory = (long) ((totalMemory - totalFreeMemory) + totalMemory * 0.75);
        final long weightMemoryOneObject = calcByteForObject(count, totalFreeMemory);
        if (weightMemoryOneObject <= 0) {
            throw new RuntimeException("weightMemoryOneObject incorrect: " + weightMemoryOneObject);
        }
        long weightSave = 0;
        System.out.println("totalMemory: " + totalMemory);
        System.out.println("limitMemory: " + limitMemory);
        System.out.println("totalFreeMemory: " + totalFreeMemory);
        System.out.println("weightMemoryOneObject: " + weightMemoryOneObject);

        for (int idx = 0; idx < loopCounter - 1; idx++) {
            Object[] array = createWeight(count);
            if (weightSave < limitMemory) {
                Object[] arc = Arrays.copyOf(array, random.nextInt((int) (count * flowFactor)));
                flow.add(arc);
                weightSave += arc.length * weightMemoryOneObject;
                if (weightSave > limitMemory) {
                    System.out.println("Stop grow by step " + (idx + 1) + " of " + loopCounter);
                    System.out.println("weightSave " + weightSave + " objects " + flow.parallelStream().mapToInt(x -> x.length).sum());
                }
            }
            Thread.sleep(10);
//            loadByCount(15000);
        }
        System.out.println("End " + flow.parallelStream().mapToInt(x -> x.length).sum());
    }

//    private void load(int millisecond) {
//        long t0 = System.currentTimeMillis();
//        while (System.currentTimeMillis() - t0 < millisecond) {
//            fillerLocal = c.update(fillerLocal);
//        }
//    }
//
//    private void loadByCount(int count) {
//        for (int i = 0; i < count; i++) {
//            fillerLocal = c.update(fillerLocal);
//            int k = fillerLocal.length;
//            k += 1;
//        }
//    }

    private long calcByteForObject(int count, long freeMemory) {
        Object[] array = createWeight(count);
        long fm = Runtime.getRuntime().freeMemory();
        System.out.println(array + " " + freeMemory + " " + Runtime.getRuntime().freeMemory());
        return (freeMemory - fm) / count;
    }

    private Object[] createWeight(int count) {
        Object[] array = new Object[count];
        for (int i = 0; i < count; i++) {
            array[i] = new String(new char[0]);
        }
        return array;
    }
}
