package ru.otus.homework.hson.processor;

import lombok.SneakyThrows;
import ru.otus.homework.hson.adpter.ObjectBuilderAdapter;

import javax.json.Json;
import javax.json.JsonObjectBuilder;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;

public class ObjectProcessor implements ValueProcessor {

    @Override
    public boolean processValue(ProcessorValueContext context) {
        var builder = Json.createObjectBuilder();
        for (Field declaredField : getAllFields(context.getValueClass())) {
            if (!declaredField.isSynthetic()) {
                processField(declaredField, builder, context);
            }
        }
        context.getBuilder().add(builder);
        return true;
    }

    private Iterable<Field> getAllFields(Class<?> valueClass) {
        var fields = new ArrayList<>(Arrays.asList(valueClass.getDeclaredFields()));
        while (valueClass.getSuperclass() != null) {
            valueClass = valueClass.getSuperclass();
            fields.addAll(Arrays.asList(valueClass.getDeclaredFields()));
        }
        return fields;
    }

    @SneakyThrows
    private void processField(Field field, JsonObjectBuilder builder, ProcessorValueContext context) {
        field.setAccessible(true);
        context.getProcessExecutor().execute(field.get(context.getValue()), new ObjectBuilderAdapter(field.getName(), builder));
    }
}
