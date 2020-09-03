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
    public void add(boolean value) {
        builder.add(field, value);
    }

    @Override
    public void add(float value) {
        builder.add(field, value);
    }

    @Override
    public void add(double value) {
        builder.add(field, value);
    }

    @Override
    public void add(long value) {
        builder.add(field, value);
    }

    @Override
    public void add(String value) {
        builder.add(field, value);
    }

    @Override
    public void add(JsonArrayBuilder value) {
        builder.add(field, value);
    }

    @Override
    public void add(JsonObjectBuilder value) {
        builder.add(field, value);
    }

    @Override
    public void add(JsonValue value) {
        builder.add(field, value);
    }
}
