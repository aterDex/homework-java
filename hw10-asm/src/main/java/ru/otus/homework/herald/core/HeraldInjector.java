package ru.otus.homework.herald.core;

import org.objectweb.asm.MethodVisitor;

public interface HeraldInjector {

    void inject(HeraldMeta heraldMeta, MethodVisitor visitor);
}
