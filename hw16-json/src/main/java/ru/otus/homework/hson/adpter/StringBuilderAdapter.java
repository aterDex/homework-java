package ru.otus.homework.hson.adpter;

import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;
import javax.json.JsonValue;

public class StringBuilderAdapter implements BuilderJsonAdapter {

    private String value = null;

    @Override
    public void add(boolean value) {
        this.value = String.valueOf(value);
    }

    @Override
    public void add(float value) {
        this.value = String.valueOf(value);
    }

    @Override
    public void add(double value) {
        this.value = String.valueOf(value);
    }

    @Override
    public void add(long value) {
        this.value = String.valueOf(value);
    }

    @Override
    public void add(String value) {
        this.value = value;
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
        this.value = value.toString();
    }

    @Override
    public void addNull() {
        value = null;
    }

    @Override
    public String toString() {
        return value;
    }
}
