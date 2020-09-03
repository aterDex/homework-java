package ru.otus.homework.hson.processor;

import org.junit.jupiter.api.Test;
import ru.otus.homework.hson.adpter.ArrayBuilderAdapter;
import ru.otus.homework.hson.adpter.BuilderJsonAdapter;
import ru.otus.homework.hson.executor.CollectionExecutor;

import javax.json.JsonArrayBuilder;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class CollectionAndArrayProcessorTest {

    CollectionAndArrayProcessor processor = new CollectionAndArrayProcessor();

    @Test
    void skipProcessor() {
        var context = new ProcessorValueContext.ProcessorValueContextBuilder()
                .value("test").valueClass(Object.class).build();
        assertFalse(processor.processValue(context));
    }

    @Test
    void executeListProcessor() {
        List<String> collectionTest = Arrays.asList("A", null, "B");
        var adapter = mock(BuilderJsonAdapter.class);
        var executor = mock(CollectionExecutor.class);
        var context = new ProcessorValueContext.ProcessorValueContextBuilder()
                .value(collectionTest).valueClass(collectionTest.getClass())
                .builder(adapter)
                .processExecutor(executor)
                .build();
        assertTrue(processor.processValue(context));
        verify(adapter).add(isA(JsonArrayBuilder.class));
        verify(executor).execute(eq("A"), isA(ArrayBuilderAdapter.class));
        verify(executor).execute(isNull(), isA(ArrayBuilderAdapter.class));
        verify(executor).execute(eq("B"), isA(ArrayBuilderAdapter.class));
        verifyNoMoreInteractions(adapter, executor);
    }

    @Test
    void executeArrayProcessor() {
        Object[] arrays = new  Object[] {"A", null, "B"};
        var adapter = mock(BuilderJsonAdapter.class);
        var executor = mock(CollectionExecutor.class);
        var context = new ProcessorValueContext.ProcessorValueContextBuilder()
                .value(arrays).valueClass(arrays.getClass())
                .builder(adapter)
                .processExecutor(executor)
                .build();
        assertTrue(processor.processValue(context));
        verify(adapter).add(isA(JsonArrayBuilder.class));
        verify(executor).execute(eq("A"), isA(ArrayBuilderAdapter.class));
        verify(executor).execute(eq("B"), isA(ArrayBuilderAdapter.class));
        verifyNoMoreInteractions(adapter);
    }

    @Test
    void executePrimitiveArrayProcessor() {
        int[] arrays = new  int[] {10, 20, -30};
        var adapter = mock(BuilderJsonAdapter.class);
        var executor = mock(CollectionExecutor.class);
        var context = new ProcessorValueContext.ProcessorValueContextBuilder()
                .value(arrays).valueClass(arrays.getClass())
                .builder(adapter)
                .processExecutor(executor)
                .build();
        assertTrue(processor.processValue(context));
        verify(adapter).add(isA(JsonArrayBuilder.class));
        verify(executor).execute(eq(10), isA(ArrayBuilderAdapter.class));
        verify(executor).execute(eq(20), isA(ArrayBuilderAdapter.class));
        verify(executor).execute(eq(-30), isA(ArrayBuilderAdapter.class));
        verifyNoMoreInteractions(adapter);
    }
}