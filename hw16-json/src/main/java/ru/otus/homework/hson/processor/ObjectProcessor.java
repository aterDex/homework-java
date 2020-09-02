package ru.otus.homework.hson.processor;

import lombok.SneakyThrows;
import ru.otus.homework.hson.adpter.ObjectBuilderAdapter;

import javax.json.Json;
import javax.json.JsonObjectBuilder;
import java.lang.reflect.Field;

public class ObjectProcessor implements ValueProcessor {

    @Override
    public boolean processValue(ProcessorContext context) {
        var builder = Json.createObjectBuilder();
        for (Field declaredField : context.getValueClass().getDeclaredFields()) {
            if (!declaredField.isSynthetic()) {
                processField(declaredField, builder, context);
            }
        }
        context.getBuilder().value(builder);
        return true;
    }

    @SneakyThrows
    private void processField(Field field, JsonObjectBuilder builder, ProcessorContext context) {
        field.setAccessible(true);
        var value = field.get(context.getValue());
        if (value == null) {
            builder.addNull(field.getName());
        } else {
            context.getCollectionExecutor().execute(value, new ObjectBuilderAdapter(field.getName(), builder));
        }
    }
}
