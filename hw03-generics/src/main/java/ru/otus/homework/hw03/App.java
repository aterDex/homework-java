package ru.otus.homework.hw03;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

public class App {

    public static void main(String[] args) {

        List<String> diYarrayList = new DIYarrayList<>();

        diYarrayList.add("01");
        diYarrayList.add("00");
        diYarrayList.add("02");
        diYarrayList.add("03");
        diYarrayList.add("04");
        diYarrayList.add("05");
        diYarrayList.add("06");
        diYarrayList.add("07");
        diYarrayList.add("08");
        diYarrayList.add("09");
        diYarrayList.add("10");
        diYarrayList.add("11");
        diYarrayList.add("12");
        diYarrayList.add("13");
        diYarrayList.add("14");

        printArray(diYarrayList);

        Collections.addAll(diYarrayList, "15", "16", "17", "18", "19");

        printArray(diYarrayList);

        List<String> list2 = new DIYarrayList<>();
        for (int i = 0; i < 20; i++) {
            list2.add(null);
        }

//        Collections.copy(list2, diYarrayList);

//        printArray(list2);
        Collections.sort(diYarrayList, String::compareTo);

        printArray(diYarrayList);
    }

    private static void printArray(List<String> diYarrayList) {
        System.out.println(diYarrayList.stream().collect(Collectors.joining(", ")));
    }

}
