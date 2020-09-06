package ru.otus.homework.hson.processor;

import lombok.SneakyThrows;

import java.util.Set;

public class IntegersProcessor implements ValueProcessor {

    private final static Set<Class<? extends Number>> SUPPORT_TYPE = Set.of(Integer.class, Long.class, Short.class, Byte.class);

    @Override
    @SneakyThrows
    public boolean processValue(ProcessorValueContext context) {
        if (!SUPPORT_TYPE.contains(context.getValueClass())) {
            return false;
        }
        context.getBuilder().add(((Number) context.getValue()).longValue());
        return true;
    }
}
