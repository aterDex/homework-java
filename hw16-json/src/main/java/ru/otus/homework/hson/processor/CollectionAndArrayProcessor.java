package ru.otus.homework.hson.processor;

import ru.otus.homework.hson.adpter.ArrayBuilderAdapter;

import javax.json.Json;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class CollectionAndArrayProcessor implements ValueProcessor {

    @Override
    public boolean processValue(ProcessorValueContext context) {
        Class<?> clazz = context.getValueClass();
        if (Collection.class.isAssignableFrom(clazz)) {
            processCollection((Collection) context.getValue(), context);
            return true;
        }
        if (clazz.isArray()) {
            Object value = context.getValue();
            List<Object> values;
            if (clazz.getComponentType().isPrimitive()) {
                values = new ArrayList<>(Array.getLength(value));
                for (int i = 0; i < Array.getLength(value); i++) {
                    values.add(Array.get(value, i));
                }
            } else {
                values = Arrays.asList((Object[]) value);
            }
            processCollection(values, context);
            return true;
        }
        return false;
    }

    private void processCollection(Collection collections, ProcessorValueContext context) {
        var arrayBuilder = Json.createArrayBuilder();
        for (Object collection : collections) {
            context.getProcessExecutor().execute(collection, new ArrayBuilderAdapter(arrayBuilder));
        }
        context.getBuilder().add(arrayBuilder);
    }
}
