package ru.otus.homework.hw08;

public class App {

    public static void main(String[] args) throws Exception {
        int loop = 30000;
        Benchmark benchmark = new Benchmark(loop);
        benchmark.run();
    }
}
