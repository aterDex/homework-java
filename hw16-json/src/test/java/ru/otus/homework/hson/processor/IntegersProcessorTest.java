package ru.otus.homework.hson.processor;

import org.junit.jupiter.api.Test;
import ru.otus.homework.hson.adpter.BuilderJsonAdapter;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class IntegersProcessorTest {

    IntegersProcessor processor = new IntegersProcessor();

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
                .value((byte) 10).valueClass(Byte.class)
                .builder(adapter)
                .build();
        assertTrue(processor.processValue(context));
        verify(adapter).add(eq(10L));
        verifyNoMoreInteractions(adapter);
        reset(adapter);

        var context1 = new ProcessorValueContext.ProcessorValueContextBuilder()
                .value((short) 11).valueClass(Short.class)
                .builder(adapter)
                .build();
        assertTrue(processor.processValue(context1));
        verify(adapter).add(eq(11L));
        verifyNoMoreInteractions(adapter);
        reset(adapter);

        var context2 = new ProcessorValueContext.ProcessorValueContextBuilder()
                .value(13).valueClass(Integer.class)
                .builder(adapter)
                .build();
        assertTrue(processor.processValue(context2));
        verify(adapter).add(eq(13L));
        verifyNoMoreInteractions(adapter);
        reset(adapter);


        var context3 = new ProcessorValueContext.ProcessorValueContextBuilder()
                .value(14L).valueClass(Long.class)
                .builder(adapter)
                .build();
        assertTrue(processor.processValue(context3));
        verify(adapter).add(eq(14L));
        verifyNoMoreInteractions(adapter);
    }

}