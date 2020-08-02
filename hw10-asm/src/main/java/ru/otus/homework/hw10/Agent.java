package ru.otus.homework.hw10;

import ru.otus.homework.herald.core.HeraldClassTransformer;

import java.lang.instrument.Instrumentation;
import java.util.Scanner;

public class Agent {

    public static void premain(String args, Instrumentation inst) {
        inst.addTransformer(new HeraldClassTransformer(args.contains("rpn")));
    }
}
