package ru.otus.homework.hson.processor;

import org.junit.jupiter.api.Test;
import ru.otus.homework.hson.adpter.BuilderJsonAdapter;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class NullProcessorTest {

    NullProcessor processor = new NullProcessor();

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
                .builder(adapter)
                .build();
        assertTrue(processor.processValue(context));
        verify(adapter).addNull();
        verifyNoMoreInteractions(adapter);
        reset(adapter);
    }
}