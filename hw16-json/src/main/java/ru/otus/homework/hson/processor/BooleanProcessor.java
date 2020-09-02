package ru.otus.homework.hson.processor;

import lombok.SneakyThrows;

public class BooleanProcessor implements ValueProcessor {

    @Override
    @SneakyThrows
    public boolean processValue(ProcessorContext context) {

        if (Boolean.class.equals(context.getValueClass())) {
            context.getBuilder().value((Boolean) context.getValue());
            return true;
        }
        return false;
    }
}
