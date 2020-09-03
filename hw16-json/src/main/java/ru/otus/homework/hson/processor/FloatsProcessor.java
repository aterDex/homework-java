package ru.otus.homework.hson.processor;

import lombok.SneakyThrows;


public class FloatsProcessor implements ValueProcessor {

    @Override
    @SneakyThrows
    public boolean processValue(ProcessorValueContext context) {

        if (Float.class.equals(context.getValueClass())) {
            context.getBuilder().add((Float) context.getValue());
            return true;
        }
        if (Double.class.equals(context.getValueClass())) {
            context.getBuilder().add((Double) context.getValue());
            return true;
        }
        return false;
    }
}
