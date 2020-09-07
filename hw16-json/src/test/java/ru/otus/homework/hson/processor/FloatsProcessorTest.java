package ru.otus.homework.hson.processor;

import org.junit.jupiter.api.Test;
import ru.otus.homework.hson.adpter.BuilderJsonAdapter;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class FloatsProcessorTest {

    FloatsProcessor processor = new FloatsProcessor();

    @Test
    void skipProcessor() {
        var context = new ProcessorValueContext.ProcessorValueContextBuilder()
                .value("test").valueClass(Object.class).build();
        assertFalse(processor.processValue(context));
    }

    @Test
    void executeProcessor() {
        var adapter = mock(BuilderJsonAdapter.class);
        var context = new ProcessorValueContext.ProcessorValueContextBuilder()
                .value(43.01f).valueClass(Float.class)
                .builder(adapter)
                .build();
        assertTrue(processor.processValue(context));
        verify(adapter).add(eq(43.01f));
        verifyNoMoreInteractions(adapter);
        reset(adapter);

        var context1 = new ProcessorValueContext.ProcessorValueContextBuilder()
                .value(-234234.2344).valueClass(Double.class)
                .builder(adapter)
                .build();
        assertTrue(processor.processValue(context1));
        verify(adapter).add(eq(-234234.2344));
        verifyNoMoreInteractions(adapter);
    }
}