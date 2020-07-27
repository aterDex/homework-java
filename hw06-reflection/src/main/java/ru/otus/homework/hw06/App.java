package ru.otus.homework.hw06;

import ru.otus.homework.hw06.test.Test01;
import ru.otus.homework.hw06.test.Test02;
import ru.otus.homework.hw06.test.secret.LoremIpsum;
import ru.otus.homework.hw06.test.Test03;
import ru.otus.homework.provoker.api.ProvokerFrameworkRunner;
import ru.otus.homework.provoker.impl.*;

import java.io.PrintStream;
import java.util.List;

public class App {

    public static void main(String[] args) {
        ProvokerFrameworkRunner provokerFrameworkRunner = initProvokerFrameworkRunnerByPackage("", System.out, false);
        provokerFrameworkRunner.run();
    }

    private static ProvokerFrameworkRunner initProvokerFrameworkRunnerByPackage(String packageNameForScan, PrintStream input, boolean printDetails) {
        DetectiveFix detectiveFix = new DetectiveFix(List.of(Test01.class, Test02.class, Test03.class, LoremIpsum.class));
        ExecutorIntoSingleThread executor = new ExecutorIntoSingleThread(new PreparerProvokersByAnnotation());
        ResultHandlerPrintStream resultHandlerPrintStream = new ResultHandlerPrintStream(System.out, printDetails);
        return new ProvokerFrameworkRunnerBase(detectiveFix, executor, resultHandlerPrintStream);
    }
}
