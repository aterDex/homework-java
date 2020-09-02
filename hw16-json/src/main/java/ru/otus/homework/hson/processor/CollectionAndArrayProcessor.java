package ru.otus.homework.hson.processor;

import ru.otus.homework.hson.adpter.ArrayBuilderAdapter;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import java.util.Arrays;
import java.util.Collection;

public class CollectionAndArrayProcessor implements ValueProcessor {

    @Override
    public boolean processValue(ProcessorContext context) {
        if (Collection.class.isAssignableFrom(context.getValueClass())) {
            processCollection((Collection) context.getValue(), context);
            return true;
        }
        if (context.getValueClass().isArray()) {
            processCollection(Arrays.asList((Object[]) context.getValue()), context);
            return true;
        }
        return false;
    }

    private void processCollection(Collection collections, ProcessorContext context) {
        JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
        for (Object collection : collections) {
            if (collection == null) {
                arrayBuilder.addNull();
            } else {
                context.getCollectionExecutor().execute(collection, new ArrayBuilderAdapter(arrayBuilder));
            }
        }
        context.getBuilder().value(arrayBuilder);
    }
}