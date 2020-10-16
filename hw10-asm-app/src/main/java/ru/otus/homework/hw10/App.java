package ru.otus.homework.hw10;

import java.util.Random;

public class App {

    public static void main(String[] args) {
        Random random = new Random(System.currentTimeMillis());
        World world = WorldBuilder.creatMediocreWorld();
        world.createSomeRandomLewdness(random, random.nextInt(5) + 1);
        world.createLittleMiracle("Nothing)");
        world.createSomeRandomLewdness(random, random.nextInt(5) + 1);
        System.out.println(world.getStatistic());
    }
}
