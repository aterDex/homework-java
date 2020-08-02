package ru.otus.homework.herald.core;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;
import ru.otus.homework.herald.api.Log;

public class HeraldDetectorMethodVisitor extends MethodVisitor {

    public static final String LOG_ANNOTATION = Log.class.getCanonicalName();

    protected final int access;
    protected final String methodName;
    protected final String methodDescriptor;

    private boolean codeHaveBeenVisited = false;
    private boolean herald = false;

    public HeraldDetectorMethodVisitor(int api, int access, String methodName, String methodDescriptor, MethodVisitor methodVisitor) {
        super(api, methodVisitor);
        this.methodName = methodName;
        this.methodDescriptor = methodDescriptor;
        this.access = access;
    }

    @Override
    public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
        if (!codeHaveBeenVisited) {
            Type t = Type.getType(descriptor);
            herald = herald || LOG_ANNOTATION.equals(t.getClassName());
        }
        return super.visitAnnotation(descriptor, visible);
    }

    @Override
    public void visitCode() {
        super.visitCode();
        codeHaveBeenVisited = true;
    }

    public boolean isHerald() {
        return herald;
    }
}
