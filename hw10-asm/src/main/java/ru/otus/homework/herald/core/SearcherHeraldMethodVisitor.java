package ru.otus.homework.herald.core;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;

import java.util.function.Consumer;

public class SearcherHeraldMethodVisitor extends HeraldDetectorMethodVisitor {

    private final Consumer<HeraldMeta> event;
    private HeraldMeta.HeraldMetaBuilder heraldMetaBuilder;

    public SearcherHeraldMethodVisitor(int api, int access, String methodName, String methodDescriptor, MethodVisitor methodVisitor, Consumer<HeraldMeta> event) {
        super(api, access, methodName, methodDescriptor, methodVisitor);
        this.event = event;
    }

    @Override
    public void visitCode() {
        super.visitCode();
        if (isHerald()) {
            heraldMetaBuilder = HeraldMeta.builder()
                    .access(getAccess())
                    .methodName(getMethodName())
                    .methodDescriptor(getMethodDescriptor());
        }
    }

    @Override
    public void visitLocalVariable(String name, String descriptor, String signature, Label start, Label end, int index) {
        super.visitLocalVariable(name, descriptor, signature, start, end, index);
        if (isHerald()) {
            heraldMetaBuilder.localVariable(
                    LocalVariableMeta.builder()
                            .name(name)
                            .descriptor(descriptor)
                            .signature(signature)
                            .index(index)
                            .build());
        }
    }

    @Override
    public void visitEnd() {
        super.visitEnd();
        if (isHerald()) {
            event.accept(heraldMetaBuilder.build());
        }
    }
}
