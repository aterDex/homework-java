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
    public void value(boolean value) {
        builder.add(value);
    }

    @Override
    public void value(float value) {
        builder.add(value);
    }

    @Override
    public void value(double value) {
        builder.add(value);
    }

    @Override
    public void value(long value) {
        builder.add(value);
    }

    @Override
    public void value(String value) {
        builder.add(value);
    }

    @Override
    public void value(JsonArrayBuilder value) {
        builder.add(value);
    }

    @Override
    public void value(JsonObjectBuilder value) {
        builder.add(value);
    }

    @Override
    public void value(JsonValue value) {
        builder.add(value);
    }
}
