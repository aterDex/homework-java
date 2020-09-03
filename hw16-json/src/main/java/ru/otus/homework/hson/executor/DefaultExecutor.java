package ru.otus.homework.hson.executor;

import ru.otus.homework.hson.processor.*;

import java.util.Collection;
import java.util.List;

public final class DefaultExecutor extends CollectionExecutor {

    private DefaultExecutor(Collection<? extends ValueProcessor> processors) {
        super(processors);
    }

    public static DefaultExecutor getExecutor() {
        return DefaultExecutorInstance.defaultExecutor;
    }

    private static final class DefaultExecutorInstance {

        private final static DefaultExecutor defaultExecutor = new DefaultExecutor(List.of(
                new NullProcessor(),
                new StringsProcessor(),
                new BooleanProcessor(),
                new FloatsProcessor(),
                new IntegersProcessor(),
                new CollectionAndArrayProcessor(),
                new ObjectProcessor()));
    }
}
