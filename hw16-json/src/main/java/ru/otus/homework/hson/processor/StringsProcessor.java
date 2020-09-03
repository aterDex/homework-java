package ru.otus.homework.hson.processor;

import lombok.SneakyThrows;

import java.util.Set;

public class StringsProcessor implements ValueProcessor {

    private final static Set<Class<?>> SUPPORT_TYPE = Set.of(Character.class, String.class);

    @Override
    @SneakyThrows
    public boolean processValue(ProcessorValueContext context) {
        if (SUPPORT_TYPE.contains(context.getValueClass())) {
            context.getBuilder().add(context.getValue().toString());
            return true;
        }
        return false;
    }
}
