package ru.otus.homework.hw10;

import ru.otus.homework.herald.api.Log;

import java.util.Arrays;
import java.util.OptionalDouble;
import java.util.Random;

public class World {

    private String firstWord;
    private int dayCounter;
    private long miracleCounter;
    private long lewdnessCounter;

    @Log
    public void sayWord(String word) {
        if (firstWord == null && word != null) {
            firstWord = word;
        }
    }

    @Log
    @Deprecated
    public void _createMatter(String text) {
        if (text != null) {
            createAtomicStructure(text);
        }
    }

    private void createAtomicStructure(String text) {
        System.out.println(Arrays.asList(text.split("\\s")));
        dayCounter++;
    }

    @Log
    public void createLittleMiracle(String miracle) {
        if (miracle != null)
            miracleCounter += miracle.length();
    }

    public void createSomeRandomLewdness(Random random, int count) {
        System.out.println("--- Somewhere, ominous laughter is heard");
        for (int i = 0; i < count; i++) {
            OptionalDouble lon = random.doubles(-180, 180)
                    .limit(1).findFirst();
            OptionalDouble lat = random.doubles(-90, 90)
                    .limit(1).findFirst();
            createSomeRandomLewdness(lat.getAsDouble(), lon.getAsDouble());
        }
    }

    @Log
    private void createSomeRandomLewdness(double lat, double lon) {
        System.out.println("Somewhere, something bad happened");
        lewdnessCounter++;
    }

    @Log
    public String getStatistic() {
        return String.format("First world was '%s' and %s day spent for create. Miracle %s. Lewdness %s.", firstWord, dayCounter, miracleCounter, lewdnessCounter);
    }
}
