package ru.otus.homework.provoker.impl;

import ru.otus.homework.provoker.api.ProvocationResultEnum;
import ru.otus.homework.provoker.api.ProvokerClassMethodResult;
import ru.otus.homework.provoker.api.ProvokerClassResult;
import ru.otus.homework.provoker.api.ResultHandler;

import java.io.PrintStream;
import java.util.Collection;

public class ResultHandlerPrintStream implements ResultHandler {

    protected static final String HEADER_CLASS = "##########################################";
    protected static final String FOOTER_CLASS = HEADER_CLASS;
    protected static final String HEADER_SUM = "%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%";
    protected static final String FOOTER_SUM = HEADER_SUM;
    protected static final String HEADER_METHOD_CLASS = "------------------------------------------";


    private PrintStream printStream;

    public ResultHandlerPrintStream(PrintStream printStream) {
        this.printStream = printStream;
    }

    public PrintStream getPrintStream() {
        return printStream;
    }

    public void setPrintStream(PrintStream printStream) {
        this.printStream = printStream;
    }

    @Override
    public void print(Collection<? extends ProvokerClassResult> results) {
        if (results == null) {
            printStream.println("There don't have any results test!");
            return;
        }
        printStatistic(results);
        results.stream().forEach(this::print);
    }

    protected void printStatistic(Collection<? extends ProvokerClassResult> results) {
        printStream.println(HEADER_SUM);
        printStream.println("Total test classes: " + results.size());
        printStream.println("Total test methods: " + results.stream().flatMap(x -> x.getTestMethodResults().stream()).count());
        for (ProvocationResultEnum result : ProvocationResultEnum.values()) {
            printStream.printf("Total with result '%s': %d", results.stream().flatMap(x -> x.getTestMethodResults().stream()).filter(x -> result.equals(x.getResult())).count());
        }
        printStream.println(FOOTER_SUM);
    }

    protected void print(ProvokerClassResult provokerClassResult) {
        printStream.println(HEADER_CLASS);
        printStream.println("Result: " + provokerClassResult.getResult());
        printStream.println("Class: " + provokerClassResult.getClazz().getPackageName());
        printStream.println("Description: " + provokerClassResult.getDescription());
        if (provokerClassResult.getDescriptionResult() != null && !provokerClassResult.getDescriptionResult().isBlank()) {
            printStream.println("DescriptionResult: " + provokerClassResult.getDescriptionResult());
        }
        printThrowable(provokerClassResult.getThrowable());
        printStream.println("Methods:");
        provokerClassResult.getTestMethodResults().stream().forEach(this::print);
        printStream.println(FOOTER_CLASS);
    }

    protected void print(ProvokerClassMethodResult provokerClassMethodResult) {
        printStream.println(HEADER_METHOD_CLASS);
        printStream.println("Result: " + provokerClassMethodResult.getResult());
        printStream.println("Method:" + provokerClassMethodResult.getMethodName());
        printStream.println("Description: " + provokerClassMethodResult.getDescription());
        if (provokerClassMethodResult.getDescriptionResult() != null && !provokerClassMethodResult.getDescriptionResult().isBlank()) {
            printStream.println("DescriptionResult: " + provokerClassMethodResult.getDescriptionResult());
        }
        printThrowable(provokerClassMethodResult.getThrowable());
    }

    protected void printThrowable(Collection<? extends Throwable> ths) {
        if (ths != null && !ths.isEmpty()) {
            printStream.println("Exceptions:");
            ths.stream().forEach(x -> {
                x.printStackTrace(printStream);
                printStream.println();
            });
        }
    }
}
