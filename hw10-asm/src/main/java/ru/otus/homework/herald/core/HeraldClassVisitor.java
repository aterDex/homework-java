package ru.otus.homework.herald.core;

import org.objectweb.asm.*;

public class HeraldClassVisitor extends ClassVisitor {

    public HeraldClassVisitor(int api) {
        super(api);
    }

    public HeraldClassVisitor(int api, ClassVisitor classVisitor) {
        super(api, classVisitor);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        return new HeraldMethodVisitor(api, access, name, descriptor, super.visitMethod(access, name, descriptor, signature, exceptions));
    }
}
