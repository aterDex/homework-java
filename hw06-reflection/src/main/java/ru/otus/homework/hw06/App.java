package ru.otus.homework.hw06;

import ru.otus.homework.provoker.api.ProvokerFrameworkRunner;
import ru.otus.homework.provoker.impl.*;

import java.io.PrintStream;

public class App {

    public static void main(String[] args) {
        ProvokerFrameworkRunner provokerFrameworkRunner =
                initProvokerFrameworkRunnerByPackage(
                        "ru.otus.homework.hw06.test",
                        "Test",
                        System.out,
                        false);
        provokerFrameworkRunner.run();
    }

    private static ProvokerFrameworkRunner initProvokerFrameworkRunnerByPackage(String packageNameForScan, String prefixFoxDetect, PrintStream output, boolean printDetails) {
        DetectiveScanPackageWithFilter detectiveScanPackageWithFilter =
                new DetectiveScanPackageWithFilter(packageNameForScan, new FilterForScanPackageByPrefix(prefixFoxDetect));
        ExecutorIntoSingleThread executor = new ExecutorIntoSingleThread(new PreparerProvokersByAnnotation());
        ResultHandlerPrintStream resultHandlerPrintStream = new ResultHandlerPrintStream(System.out, printDetails);
        return new ProvokerFrameworkRunnerBase(detectiveScanPackageWithFilter, executor, resultHandlerPrintStream);
    }
}
