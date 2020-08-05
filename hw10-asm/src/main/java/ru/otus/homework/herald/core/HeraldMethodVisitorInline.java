package ru.otus.homework.herald.core;

import org.objectweb.asm.MethodVisitor;

public class HeraldMethodVisitorInline extends HeraldDetectorMethodVisitor {

    private final HeraldMeta heraldMeta;
    /**
     * Так как в обработку может передастся этот класс, вызов visitCode(вызываеть его быссмысленно но на всякий случий)
     * внутри может привести к циклу, для этого делаем барьер.
     */
    private boolean visitCodeBarrier = false;

    public HeraldMethodVisitorInline(int api, int access, String methodName, String methodDescriptor, HeraldMeta heraldMeta, MethodVisitor methodVisitor) {
        super(api, access, methodName, methodDescriptor, methodVisitor);
        this.heraldMeta = heraldMeta;
    }

    @Override
    public void visitCode() {
        super.visitCode();
        try {
            if (!visitCodeBarrier && (heraldMeta != null || isHerald())) {
                visitCodeBarrier = true;
                new HeraldInjectorParametersLogBySystemOut().inject(resolveHeraldMeta(), this);
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
                        .comment(getComment())
                        .printProperty(isPrintProperty())
                        .build();
    }
}
