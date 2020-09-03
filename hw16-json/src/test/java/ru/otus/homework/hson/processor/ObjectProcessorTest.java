package ru.otus.homework.hson.processor;

import org.junit.jupiter.api.Test;
import ru.otus.homework.hson.adpter.BuilderJsonAdapter;
import ru.otus.homework.hson.adpter.ObjectBuilderAdapter;
import ru.otus.homework.hson.executor.CollectionExecutor;

import javax.json.JsonObjectBuilder;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class ObjectProcessorTest {

    ObjectProcessor processor = new ObjectProcessor();

    @Test
    void executeProcessor() {
        var obj = new Object() {
            Object a = null;
            Object b = "test";
        };
        var adapter = mock(BuilderJsonAdapter.class);
        var executor = mock(CollectionExecutor.class);
        var context = new ProcessorValueContext.ProcessorValueContextBuilder()
                .value(obj).valueClass(obj.getClass())
                .builder(adapter)
                .processExecutor(executor)
                .build();
        assertTrue(processor.processValue(context));
        verify(adapter).add(isA(JsonObjectBuilder.class));
        verify(executor).execute(eq("test"), isA(ObjectBuilderAdapter.class));
        verifyNoMoreInteractions(adapter, executor);
    }

    @Test
    void testInheritedField() {
        var adapter = mock(BuilderJsonAdapter.class);
        var executor = mock(CollectionExecutor.class);
        var context = new ProcessorValueContext.ProcessorValueContextBuilder()
                .value(new Second()).valueClass(Second.class)
                .builder(adapter)
                .processExecutor(executor)
                .build();
        assertTrue(processor.processValue(context));
        verify(adapter).add(isA(JsonObjectBuilder.class));
        verify(executor).execute(eq("A"), isA(ObjectBuilderAdapter.class));
        verify(executor).execute(eq("B"), isA(ObjectBuilderAdapter.class));
        verifyNoMoreInteractions(adapter, executor);
    }

    public static class First {
        public String a = "A";
    }

    public static class Second extends First {
        public String b = "B";
    }
}