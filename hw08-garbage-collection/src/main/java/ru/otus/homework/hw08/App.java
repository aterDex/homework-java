package ru.otus.homework.hw08;

public class App {

    public static void main(String[] args) throws Exception {
        int loop = 30000;
        BenchmarkLoaderRef benchmark = new BenchmarkLoaderRef(loop);
        benchmark.run();
    }
}
