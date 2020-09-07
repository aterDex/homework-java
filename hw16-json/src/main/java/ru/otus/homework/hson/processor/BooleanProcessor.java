package ru.otus.homework.hson.processor;

import lombok.SneakyThrows;

public class BooleanProcessor implements ValueProcessor {

    @Override
    @SneakyThrows
    public boolean processValue(ProcessorValueContext context) {

        if (Boolean.class.equals(context.getValueClass())) {
            context.getBuilder().add((Boolean) context.getValue());
            return true;
        }
        return false;
    }
}
