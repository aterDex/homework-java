package ru.otus.homework.hson.adpter;

import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;
import javax.json.JsonValue;

public interface BuilderJsonAdapter {

    void add(boolean value);

    void add(float value);

    void add(double value);

    void add(long value);

    void add(String value);

    void add(JsonArrayBuilder value);

    void add(JsonObjectBuilder value);

    void add(JsonValue value);

    void addNull();
}
