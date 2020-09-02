package ru.otus.homework.hson.processor;

import lombok.SneakyThrows;


public class FloatsProcessor implements ValueProcessor {

    @Override
    @SneakyThrows
    public boolean processValue(ProcessorContext context) {

        if (Float.class.equals(context.getValueClass())) {
            context.getBuilder().value((Float) context.getValue());
            return true;
        }
        if (Double.class.equals(context.getValueClass())) {
            context.getBuilder().value((Double) context.getValue());
            return true;
        }
        return false;
    }
}
