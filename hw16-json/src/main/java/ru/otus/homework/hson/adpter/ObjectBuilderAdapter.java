package ru.otus.homework.hson.adpter;

import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;
import javax.json.JsonValue;

public class ObjectBuilderAdapter implements BuilderJsonAdapter {

    private final String field;
    private final JsonObjectBuilder builder;

    public ObjectBuilderAdapter(String field, JsonObjectBuilder builder) {
        this.field = field;
        this.builder = builder;
    }

    @Override
    public void value(boolean value) {
        builder.add(field, value);
    }

    @Override
    public void value(float value) {
        builder.add(field, value);
    }

    @Override
    public void value(double value) {
        builder.add(field, value);
    }

    @Override
    public void value(long value) {
        builder.add(field, value);
    }

    @Override
    public void value(String value) {
        builder.add(field, value);
    }

    @Override
    public void value(JsonArrayBuilder value) {
        builder.add(field, value);
    }

    @Override
    public void value(JsonObjectBuilder value) {
        builder.add(field, value);
    }

    @Override
    public void value(JsonValue value) {
        builder.add(field, value);
    }
}
