package ru.otus.homework.hson.processor;

public class NullProcessor implements ValueProcessor {

    @Override
    public boolean processValue(ProcessorValueContext context) {
        if (context.getValue() == null) {
            context.getBuilder().addNull();
            return true;
        }
        return false;
    }
}
