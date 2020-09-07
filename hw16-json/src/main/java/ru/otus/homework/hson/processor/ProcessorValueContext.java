package ru.otus.homework.hson.processor;

import lombok.Builder;
import lombok.Value;
import ru.otus.homework.hson.adpter.BuilderJsonAdapter;
import ru.otus.homework.hson.executor.ProcessExecutor;

@Value
@Builder
public class ProcessorValueContext {

    private Object value;
    private Class<?> valueClass;
    private BuilderJsonAdapter builder;
    private ProcessExecutor processExecutor;
}
