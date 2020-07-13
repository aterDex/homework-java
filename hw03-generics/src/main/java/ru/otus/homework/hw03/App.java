package ru.otus.homework.hw03;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Проверка DIYarrayList
 * Проверка будет происходить путем сравнения результатов работы само писанного листа,
 * и модельного листа (на основе ArrayList)
 */
public class App {

    public static final String STATUS_FAILED = "status: FAILED";

    public static void main(String[] args) {
        // счетчик проверок
        int generation = 0;

        // 1. Создаем наполнитель для тестирования
        Random random = new Random(49648640);
        Integer[] filler0 = random
                .ints(10000, -5000, 5000)
                .mapToObj(Integer::valueOf)
                .toArray(Integer[]::new);
        Integer[] filler1 = random
                .ints(100, 10000, 20000)
                .mapToObj(Integer::valueOf)
                .toArray(Integer[]::new);
        List<Integer> filler2 = random
                .ints(50, 30000, 300000)
                .mapToObj(Integer::valueOf)
                .collect(Collectors.toList());


        // 2. Создаем DIY и эталонный лист
        List<Integer> diyList = new DIYarrayList<>();
        List<Integer> modelList = new ArrayList<>();

        // 3. Проверяем Collections.addAll на пустой коллекции
        Collections.addAll(diyList, filler0);
        Collections.addAll(modelList, filler0);
        compareAndPrintResult(diyList, modelList, ++generation, "check method Collections.addAll into empty collection");

        // 4. Проверяем Collections.addAll на не пустой коллекции
        Collections.addAll(diyList, filler1);
        Collections.addAll(modelList, filler1);
        compareAndPrintResult(diyList, modelList, ++generation, "check method Collections.addAll into not empty collection");

        // 5. Проверяем Collections.copy
        Collections.copy(diyList, filler2);
        Collections.copy(modelList, filler2);
        compareAndPrintResult(diyList, modelList, ++generation, "check method Collections.copy");

        // 6. Проверяем Collections.sort
        Collections.sort(diyList, Integer::compareTo);
        Collections.sort(modelList, Integer::compareTo);
        compareAndPrintResult(diyList, modelList, ++generation, "check method Collections.sort");
    }

    private static <T> void compareAndPrintResult(List<? extends T> diyList, List<? extends T> modelList, int generation, String description) {
        System.out.println("======= generation " + generation + " =======");
        System.out.println("description: " + (description == null ? "" : description));
        System.out.format("size DIY: %1$d model: %2$d", diyList.size(), modelList.size());
        System.out.println();
        if (diyList.size() != modelList.size()) {
            System.out.println("size difference!");
            System.out.println(STATUS_FAILED);
            System.out.println();
            return;
        }

        System.out.println("comparing all elements...");
        for (int i = 0; i < diyList.size(); i++) {
            Object diyElement = diyList.get(i);
            Object modelElement = modelList.get(i);
            if (!Objects.equals(diyElement, modelElement)) {
                System.out.format("stop check in position %1$d DIY: %2$s model: %3$s", i, diyElement, modelElement);
                System.out.println();
                System.out.println(STATUS_FAILED);
                System.out.println();
                return;
            }
        }
        System.out.println("status: OK");
        System.out.println();
    }
}
