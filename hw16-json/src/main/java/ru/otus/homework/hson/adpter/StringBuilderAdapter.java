package ru.otus.homework.hson.adpter;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;
import javax.json.JsonValue;

public class StringBuilderAdapter implements BuilderJsonAdapter {

    private String value;

    @Override
    public void value(boolean value) {
//        value(Json.createValue(value));
    }

    @Override
    public void value(float value) {
        value(Json.createValue(value));
    }

    @Override
    public void value(double value) {
        value(Json.createValue(value));
    }

    @Override
    public void value(long value) {
        value(Json.createValue(value));
    }

    @Override
    public void value(String value) {
        value(Json.createValue(value));
    }

    @Override
    public void value(JsonArrayBuilder value) {
        this.value = String.valueOf(value.build());
    }

    @Override
    public void value(JsonObjectBuilder value) {
        this.value = String.valueOf(value.build());
    }

    @Override
    public void value(JsonValue value) {
        this.value = String.valueOf(value.asJsonObject());
    }

    @Override
    public String toString() {
        return value;
    }
}
