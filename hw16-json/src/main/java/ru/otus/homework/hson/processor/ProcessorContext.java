package ru.otus.homework.hson.processor;

import lombok.Builder;
import lombok.Value;
import ru.otus.homework.hson.executor.CollectionExecutor;
import ru.otus.homework.hson.adpter.BuilderJsonAdapter;

@Value
@Builder
public class ProcessorContext {

    private Object value;
    private Class<?> valueClass;
    private BuilderJsonAdapter builder;
    private CollectionExecutor collectionExecutor;
}
