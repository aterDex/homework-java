package ru.otus.homework.hson.processor;

import org.junit.jupiter.api.Test;
import ru.otus.homework.hson.adpter.BuilderJsonAdapter;
import ru.otus.homework.hson.executor.CollectionExecutor;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class BooleanProcessorTest {

    BooleanProcessor processor = new BooleanProcessor();

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
                .value(true).valueClass(Boolean.class)
                .builder(adapter)
                .build();
        assertTrue(processor.processValue(context));
        verify(adapter).add(eq(true));
        verifyNoMoreInteractions(adapter);
        reset(adapter);

        var context1 = new ProcessorValueContext.ProcessorValueContextBuilder()
                .value(false).valueClass(Boolean.class)
                .builder(adapter)
                .build();
        assertTrue(processor.processValue(context1));
        verify(adapter).add(eq(false));
        verifyNoMoreInteractions(adapter);
    }
}