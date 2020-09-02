package ru.otus.homework.hson.executor;

import ru.otus.homework.hson.adpter.BuilderJsonAdapter;
import ru.otus.homework.hson.processor.ValueProcessor;
import ru.otus.homework.hson.processor.ProcessorContext;

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
            if (processor.processValue(ProcessorContext.builder()
                    .value(value)
                    .builder(builder)
                    .valueClass(value.getClass())
                    .collectionExecutor(this)
                    .build()))
                break;
        }
    }

    public Collection<? extends ValueProcessor> getProcessors() {
        return processors;
    }
}
