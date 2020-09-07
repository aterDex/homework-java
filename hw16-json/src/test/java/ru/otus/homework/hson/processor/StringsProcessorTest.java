package ru.otus.homework.hson.processor;

import org.junit.jupiter.api.Test;
import ru.otus.homework.hson.adpter.BuilderJsonAdapter;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class StringsProcessorTest {

    StringsProcessor processor = new StringsProcessor();

    @Test
    void skipProcessor() {
        var context = new ProcessorValueContext.ProcessorValueContextBuilder()
                .value(0L).valueClass(Object.class).build();
        assertFalse(processor.processValue(context));
    }

    @Test
    void executeProcessor() {
        var adapter = mock(BuilderJsonAdapter.class);
        var context = new ProcessorValueContext.ProcessorValueContextBuilder()
                .value("test").valueClass(String.class)
                .builder(adapter)
                .build();
        assertTrue(processor.processValue(context));
        verify(adapter).add(eq("test"));
        verifyNoMoreInteractions(adapter);
        reset(adapter);

        var context1 = new ProcessorValueContext.ProcessorValueContextBuilder()
                .value('t').valueClass(Character.class)
                .builder(adapter)
                .build();
        assertTrue(processor.processValue(context1));
        verify(adapter).add(eq("t"));
        verifyNoMoreInteractions(adapter);
    }
}