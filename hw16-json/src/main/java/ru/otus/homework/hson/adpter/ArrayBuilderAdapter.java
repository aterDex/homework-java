package ru.otus.homework.hson.adpter;

import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;
import javax.json.JsonValue;

public class ArrayBuilderAdapter implements BuilderJsonAdapter {

    private final JsonArrayBuilder builder;

    public ArrayBuilderAdapter(JsonArrayBuilder builder) {
        this.builder = builder;
    }

    @Override
    public void add(boolean value) {
        builder.add(value);
    }

    @Override
    public void add(float value) {
        builder.add(value);
    }

    @Override
    public void add(double value) {
        builder.add(value);
    }

    @Override
    public void add(long value) {
        builder.add(value);
    }

    @Override
    public void add(String value) {
        builder.add(value);
    }

    @Override
    public void add(JsonArrayBuilder value) {
        builder.add(value);
    }

    @Override
    public void add(JsonObjectBuilder value) {
        builder.add(value);
    }

    @Override
    public void add(JsonValue value) {
        builder.add(value);
    }
}
