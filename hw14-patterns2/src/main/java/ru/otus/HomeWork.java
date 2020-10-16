package ru.otus;

import ru.otus.handler.ComplexProcessor;
import ru.otus.listener.ListenerHistoryWithFixDeep;
import ru.otus.processor.ProcessorConcatFields;
import ru.otus.processor.ProcessorExceptionIfEvenSecond;
import ru.otus.processor.ProcessorSwapField11AndField13;
import ru.otus.processor.ProcessorUpperField10;

import java.util.List;

public class HomeWork {

    public static void main(String[] args) throws Exception {

        var processors = List.of(new ProcessorConcatFields(),
                new ProcessorExceptionIfEvenSecond(new ProcessorSwapField11AndField13(), System::currentTimeMillis),
                new ProcessorUpperField10());

        var complexProcessor = new ComplexProcessor(processors, (ex) -> {
            ex.printStackTrace();
        });
        var listener = new ListenerHistoryWithFixDeep();
        complexProcessor.addListener(listener);

        var message = new Message.Builder()
                .field1("field1")
                .field2("field2")
                .field3("field3")
                .field6("field6")
                .field10("field10")
                .field13("-- 13 --")
                .field12("-- 12 --")
                .field11("-- 11 --")
                .build();

        for (int i = 0; i < 3; i++) {
            message = complexProcessor.handle(message);
            Thread.sleep(1000);
        }
        System.out.println("history: ");
        listener.getHistory().forEach(System.out::println);
        System.out.println("============================");

        complexProcessor.removeListener(listener);
    }
}
