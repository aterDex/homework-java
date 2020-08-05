package ru.otus.homework.hw10;

import ru.otus.homework.herald.core.HeraldClassTransformerInline;

import java.lang.instrument.Instrumentation;

public class AgentInline {

    public static void premain(String args, Instrumentation inst) {
        inst.addTransformer(new HeraldClassTransformerInline(args != null && args.contains("rpn")));
    }
}
