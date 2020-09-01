package ru.otus.processor;

import ru.otus.Message;

public class ProcessorSwapField11AndField13 implements Processor {

    @Override
    public Message process(Message message) {
        return message.toBuilder()
                .field11(message.getField13())
                .field13(message.getField11())
                .build();
    }
}
