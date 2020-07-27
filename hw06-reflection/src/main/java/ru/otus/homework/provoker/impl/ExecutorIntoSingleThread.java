package ru.otus.homework.provoker.impl;

import ru.otus.homework.provoker.api.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ExecutorIntoSingleThread implements Executor {

    private final PreparerProvokers preparerProvokers;

    public ExecutorIntoSingleThread(PreparerProvokers preparerProvokers) {
        this.preparerProvokers = preparerProvokers;
    }

    @Override
    public ProvokerClassResult execute(Class aClass) {
        ProvokerClass provokerClass;
        try {
            provokerClass = preparerProvokers.prepare(aClass);
        } catch (Throwable t) {
            return createIllegalResult(t, aClass);
        }

        List<ProvokerClassMethodResult> methodsResults = new ArrayList<>(provokerClass.getTestMethods().size());
        Optional<Throwable> beforeAll, afterAll;

        beforeAll = provokerClass.beforeAll();
        if (!beforeAll.isEmpty()) {
            methodsResults.addAll(createAllSkipTestMethodResult(provokerClass));
        } else {
            for (ProvokerClassMethod testMethod : provokerClass.getTestMethods()) {
                Optional<Throwable> init, before, after, test;
                init = testMethod.init();
                before = testMethod.before();
                test = before.isEmpty() ? testMethod.test() : Optional.empty();
                after = testMethod.after();
                methodsResults.add(createMethodResult(testMethod, init, before, test, after));
            }
        }
        afterAll = provokerClass.afterAll();
        return createResult(beforeAll, afterAll, provokerClass, methodsResults);
    }

    private Collection<ProvokerClassMethodResult> createAllSkipTestMethodResult(ProvokerClass provokerClass) {
        return provokerClass.getTestMethods().stream()
                .map(x -> ProvokerClassMethodResultImmutable
                        .builder()
                        .methodName(x.getMethodName())
                        .description(provokerClass.getDescription())
                        .result(ProvocationResultEnum.SKIP)
                        .build()
                ).collect(Collectors.toList());
    }

    private ProvokerClassMethodResult createMethodResult(
            ProvokerClassMethod testMethod,
            Optional<Throwable> init,
            Optional<Throwable> before,
            Optional<Throwable> test,
            Optional<Throwable> after) {

        ProvokerClassMethodResultImmutable.TestMethodResultImmutableBuilder builder = ProvokerClassMethodResultImmutable.builder();
        builder
                .methodName(testMethod.getMethodName())
                .description(testMethod.getDescription());

        List<Throwable> listThrow = Stream.of(init, before, test, after)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());

        if (listThrow.isEmpty()) {
            builder
                    .result(ProvocationResultEnum.OK);
        } else {
            builder
                    .result(ProvocationResultEnum.FAILED)
                    .throwable(listThrow);
        }
        return builder.build();
    }

    private ProvokerClassResult createResult(Optional<Throwable> beforeAll, Optional<Throwable> afterAll, ProvokerClass provokerClass, List<ProvokerClassMethodResult> results) {
        List<Throwable> listThrow = Stream.of(beforeAll, afterAll)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
        ProvocationResultEnum result =
                !listThrow.isEmpty() || results.stream()
                        .filter(x -> x.getResult() == ProvocationResultEnum.FAILED)
                        .findAny().isPresent()
                        ? ProvocationResultEnum.FAILED : ProvocationResultEnum.OK;

        return ProvokerClassResultImmutable.builder()
                .clazz(provokerClass.getClazz())
                .name(provokerClass.getClazz().getName())
                .description(provokerClass.getDescription())
                .result(result)
                .testMethodResults(results == null ? createAllSkipTestMethodResult(provokerClass) : results)
                .throwable(listThrow)
                .build();
    }

    private ProvokerClassResult createIllegalResult(Throwable throwable, Class aClass) {
        return ProvokerClassResultImmutable.builder()
                .clazz(aClass)
                .name(aClass.getName())
                .throwable(List.of(throwable))
                .result(ProvocationResultEnum.ILLEGAL)
                .build();
    }
}