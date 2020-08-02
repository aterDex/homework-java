package ru.otus.homework.hw10;

import ru.otus.homework.herald.core.HeraldClassTransformer;

import java.lang.instrument.Instrumentation;

public class Agent {

    public static void premain(String args, Instrumentation inst) {
        inst.addTransformer(new HeraldClassTransformer(args != null && args.contains("rpn")));
    }
}
