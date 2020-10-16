package ru.otus.processor;

import ru.otus.Message;

import java.util.function.Supplier;

public class ProcessorExceptionIfEvenSecond implements Processor {

    private final Supplier<Long> millisecondProvider;
    private final Processor processor;

    public ProcessorExceptionIfEvenSecond(Processor processor, Supplier<Long> millisecondProvider) {
        if (millisecondProvider == null) throw new IllegalArgumentException("millisecondProvider mustn't be null.");
        this.millisecondProvider = millisecondProvider;
        this.processor = processor;
    }

    @Override
    public Message process(Message message) {
        Long millisecond = millisecondProvider.get();
        if (millisecond != null) {
            if (millisecond / 1000 % 2 == 0) throw new RuntimeException("Even second exception");
        }
        return processor == null ? message : processor.process(message);
    }
}
