package ru.otus.homework.herald.core;

import lombok.Getter;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;
import ru.otus.homework.herald.api.Log;

public class HeraldDetectorMethodVisitor extends MethodVisitor {

    public static final String LOG_ANNOTATION = Log.class.getCanonicalName();

    @Getter
    private final int access;
    @Getter
    private final String methodName;
    @Getter
    private final String methodDescriptor;
    @Getter
    private boolean herald = false;
    @Getter
    private boolean printProperty = true;
    @Getter
    private String comment = null;
    private boolean codeHaveBeenVisited = false;

    public HeraldDetectorMethodVisitor(int api, int access, String methodName, String methodDescriptor, MethodVisitor methodVisitor) {
        super(api, methodVisitor);
        this.methodName = methodName;
        this.methodDescriptor = methodDescriptor;
        this.access = access;
    }

    @Override
    public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
        AnnotationVisitor defaultVisitor = super.visitAnnotation(descriptor, visible);
        if (!codeHaveBeenVisited) {
            Type t = Type.getType(descriptor);
            if (LOG_ANNOTATION.equals(t.getClassName())) {
                herald = true;
                return new HeraldLogAnnotationVisitor(api, defaultVisitor);
            }
        }
        return defaultVisitor;
    }

    @Override
    public void visitCode() {
        super.visitCode();
        codeHaveBeenVisited = true;
    }

    private class HeraldLogAnnotationVisitor extends AnnotationVisitor {

        public HeraldLogAnnotationVisitor(int api, AnnotationVisitor annotationVisitor) {
            super(api, annotationVisitor);
        }

        @Override
        public void visit(String name, Object value) {
            if (value != null) {
                switch (name) {
                    case "printProperty":
                        if (value instanceof Boolean) {
                            printProperty = (Boolean) value;
                        }
                        break;
                    case "comment":
                        comment = value.toString();
                        break;
                }
            }
            super.visit(name, value);
        }
    }
}
