package ru.otus.homework.hw10;

import ru.otus.homework.herald.core.HeraldClassTransformerLogMethod;

import java.lang.instrument.Instrumentation;

public class AgentLogMethod {

    public static void premain(String args, Instrumentation inst) {
        inst.addTransformer(new HeraldClassTransformerLogMethod());
    }
}
