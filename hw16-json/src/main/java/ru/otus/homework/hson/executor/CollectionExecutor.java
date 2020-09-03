package ru.otus.homework.hson.executor;

import ru.otus.homework.hson.adpter.BuilderJsonAdapter;
import ru.otus.homework.hson.processor.ValueProcessor;
import ru.otus.homework.hson.processor.ProcessorValueContext;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class CollectionExecutor implements ProcessExecutor {

    private final Collection<? extends ValueProcessor> processors;

    public CollectionExecutor(Collection<? extends ValueProcessor> processors) {
        this.processors = Collections.unmodifiableCollection(new ArrayList<>(processors));
    }

    @Override
    public void execute(Object value, BuilderJsonAdapter builder) {
        for (ValueProcessor processor : processors) {
            if (processor.processValue(ProcessorValueContext.builder()
                    .value(value)
                    .builder(builder)
                    .valueClass(value == null ? null : value.getClass())
                    .processExecutor(this)
                    .build()))
                break;
        }
    }

    public Collection<? extends ValueProcessor> getProcessors() {
        return processors;
    }
}
