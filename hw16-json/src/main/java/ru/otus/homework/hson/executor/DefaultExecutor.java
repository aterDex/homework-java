package ru.otus.homework.hson.executor;

import ru.otus.homework.hson.processor.*;

import java.util.Collection;
import java.util.List;

public final class DefaultExecutor {

    public static ProcessExecutor getExecutor() {
        return DefaultExecutorInstance.defaultExecutor;
    }

    private static final class DefaultExecutorInstance {

        private final static ProcessExecutor defaultExecutor = new CollectionExecutor(List.of(
                new NullProcessor(),
                new StringsProcessor(),
                new BooleanProcessor(),
                new FloatsProcessor(),
                new IntegersProcessor(),
                new CollectionAndArrayProcessor(),
                new ObjectProcessor()));
    }
}
