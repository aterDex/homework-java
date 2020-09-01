package ru.otus.processor;

import ru.otus.Message;

public class ProcessorUpperField10 implements Processor {

    @Override
    public Message process(Message message) {
        return message.toBuilder().field10(message.getField10().toUpperCase()).build();
    }
}
