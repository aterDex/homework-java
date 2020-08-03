package ru.otus.homework.herald.core;

import org.objectweb.asm.MethodVisitor;

import java.util.Optional;
import java.util.function.Supplier;

public class HeraldMethodVisitor extends HeraldDetectorMethodVisitor {


    private final HeraldMeta heraldMeta;
    private final Supplier<Optional<HeraldInjector>> injectorForMethodFactory;
    /**
     * Так как в обработку может передастся этот класс, вызов visitCode(вызываеть его быссмысленно но на всякий случий)
     * внутри может привести к циклу, для этого делаем барьер.
     */
    private boolean visitCodeBarrier = false;

    public HeraldMethodVisitor(int api, int access, String methodName, String methodDescriptor, HeraldMeta heraldMeta, Supplier<Optional<HeraldInjector>> injectorForMethodFactory, MethodVisitor methodVisitor) {
        super(api, access, methodName, methodDescriptor, methodVisitor);
        assert injectorForMethodFactory != null;
        this.heraldMeta = heraldMeta;
        this.injectorForMethodFactory = injectorForMethodFactory;
    }

    @Override
    public void visitCode() {
        super.visitCode();
        try {
            if (!visitCodeBarrier && (heraldMeta != null || isHerald())) {
                visitCodeBarrier = true;
                injectorForMethodFactory.get().ifPresent(x -> x.inject(resolveHeraldMeta(), this));
            }
        } finally {
            visitCodeBarrier = false;
        }
    }

    private HeraldMeta resolveHeraldMeta() {
        return heraldMeta != null ? heraldMeta :
                HeraldMeta.builder()
                        .access(getAccess())
                        .methodName(getMethodName())
                        .methodDescriptor(getMethodDescriptor())
                        .build();
    }
}
