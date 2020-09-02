package ru.otus.homework.hson.adpter;

import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;
import javax.json.JsonValue;

public interface BuilderJsonAdapter {

    void value(boolean value);

    void value(float value);

    void value(double value);

    void value(long value);

    void value(String value);

    void value(JsonArrayBuilder value);

    void value(JsonObjectBuilder value);

    void value(JsonValue value);
}
