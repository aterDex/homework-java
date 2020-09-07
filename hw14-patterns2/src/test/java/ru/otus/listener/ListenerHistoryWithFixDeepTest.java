package ru.otus.listener;

import org.junit.jupiter.api.Test;
import ru.otus.Message;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ListenerHistoryWithFixDeepTest {

    @Test
    void onUpdated() {
        var message0 = new Message.Builder().field1("field1").build();
        var message1 = new Message.Builder().field13("field13").build();
        var history = new ListenerHistoryWithFixDeep(5);

        assertEquals(0, history.getHistory().size());
        history.onUpdated(message0, message1);
        assertEquals(1, history.getHistory().size());
        history.onUpdated(message1, message0);
        history.onUpdated(message0, message1);
        history.onUpdated(message1, message0);
        assertEquals(4, history.getHistory().size());
        history.onUpdated(message0, message1);
        assertEquals(5, history.getHistory().size());
        history.onUpdated(message1, message0);
        history.onUpdated(message0, message1);
        assertEquals(5, history.getHistory().size());

        var recordFirst0 = history.getHistory().iterator().next();
        assertEquals(message1, recordFirst0.getNewMsg());
        assertEquals(message0, recordFirst0.getOldMsg());

        history.onUpdated(message1, message0);

        var recordFirst1 = history.getHistory().iterator().next();
        assertEquals(message0, recordFirst1.getNewMsg());
        assertEquals(message1, recordFirst1.getOldMsg());
    }
}