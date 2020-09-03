package ru.otus.homework.hson.adpter;

import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;
import javax.json.JsonValue;

public class StringBuilderAdapter implements BuilderJsonAdapter {

    private String value;

    @Override
    public void add(boolean value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void add(float value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void add(double value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void add(long value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void add(String value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void add(JsonArrayBuilder value) {
        this.value = String.valueOf(value.build());
    }

    @Override
    public void add(JsonObjectBuilder value) {
        this.value = String.valueOf(value.build());
    }

    @Override
    public void add(JsonValue value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String toString() {
        return value;
    }
}
