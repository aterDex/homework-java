package ru.otus.processor;

import org.junit.jupiter.api.Test;
import ru.otus.Message;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ProcessorSwapField11AndField13Test {

    @Test
    void process() {
        ProcessorSwapField11AndField13 field13 = new ProcessorSwapField11AndField13();

        Message message = new Message.Builder()
                .field12("field12")
                .field11("field11")
                .field13("field13")
                .build();

        Message anotherMessage = field13.process(message);
        assertEquals(message.getField12(), anotherMessage.getField12());
        assertEquals(message.getField11(), anotherMessage.getField13());
        assertEquals(message.getField13(), anotherMessage.getField11());
    }
}