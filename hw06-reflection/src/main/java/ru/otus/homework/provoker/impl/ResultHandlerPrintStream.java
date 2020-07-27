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
    private boolean printDetails = false;

    public ResultHandlerPrintStream(PrintStream printStream) {
        this.printStream = printStream;
    }

    public ResultHandlerPrintStream(PrintStream printStream, boolean printDetails) {
        this.printStream = printStream;
        this.printDetails = printDetails;
    }

    public PrintStream getPrintStream() {
        return printStream;
    }

    public void setPrintStream(PrintStream printStream) {
        this.printStream = printStream;
    }

    @Override
    public void print(Collection<? extends ProvokerClassResult> results) {
        printStream.println();
        if (results == null || results.isEmpty()) {
            printStream.println("There don't have any results test!");
            return;
        } else {
            printStream.println("Ok, there have some results to say about tests!");
        }
        printStatistic(results);
        if (printDetails) {
            results.stream().forEach(this::print);
        }
    }

    protected void printStatistic(Collection<? extends ProvokerClassResult> results) {
        printStream.println(HEADER_SUM);
        printStream.println("Total test classes: " + results.size());
        printStream.println("Total test methods: " + results.stream().flatMap(x -> x.getTestMethodResults().stream()).count());
        for (ProvocationResultEnum result : ProvocationResultEnum.values()) {
            printStream.printf("Total methods with result '%s': %d", result, results.stream().flatMap(x -> x.getTestMethodResults().stream()).filter(x -> result.equals(x.getResult())).count())
                    .println();
        }
        printStream.println(FOOTER_SUM);
    }

    protected void print(ProvokerClassResult provokerClassResult) {
        printStream.println(HEADER_CLASS);
        printStream.println("Result: " + provokerClassResult.getResult());
        printStream.println("Class: " + provokerClassResult.getClazz().getCanonicalName());
        if (provokerClassResult.getDescription() != null && !provokerClassResult.getDescription().isBlank()) {
            printStream.println("Description: " + provokerClassResult.getDescription());
        }
        if (provokerClassResult.getDescriptionResult() != null && !provokerClassResult.getDescriptionResult().isBlank()) {
            printStream.println("DescriptionResult: " + provokerClassResult.getDescriptionResult());
        }
        printThrowable(provokerClassResult.getThrowable());
        if (provokerClassResult.getTestMethodResults() != null && !provokerClassResult.getTestMethodResults().isEmpty()) {
            printStream.println("Methods:");
            provokerClassResult.getTestMethodResults().stream().forEach(this::print);
        } else {
            printStream.println("There don't have any results test methods!");
        }
        printStream.println(FOOTER_CLASS);
    }

    protected void print(ProvokerClassMethodResult provokerClassMethodResult) {
        printStream.println(HEADER_METHOD_CLASS);
        printStream.println("Result: " + provokerClassMethodResult.getResult());
        printStream.println("Method: " + provokerClassMethodResult.getMethodName());
        if (provokerClassMethodResult.getDescription() != null && !provokerClassMethodResult.getDescription().isBlank()) {
            printStream.println("Description: " + provokerClassMethodResult.getDescription());
        }
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

    public boolean isPrintDetails() {
        return printDetails;
    }

    public void setPrintDetails(boolean printDetails) {
        this.printDetails = printDetails;
    }
}
