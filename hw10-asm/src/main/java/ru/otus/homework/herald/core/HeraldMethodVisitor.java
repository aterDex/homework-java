package ru.otus.homework.herald.core;

import org.objectweb.asm.*;
import ru.otus.homework.herald.api.Log;

import java.lang.reflect.Modifier;

public class HeraldMethodVisitor extends MethodVisitor {

    public static final String LOG_ANNOTATION = Log.class.getCanonicalName();
    public static final Type TYPE_STRING_BUILDER = Type.getType(StringBuilder.class);

    private final int access;
    private final String methodName;
    private final String methodDescriptor;

    private boolean codeHaveBeenVisited = false;
    private boolean addLog = false;

    public HeraldMethodVisitor(int api, int access, String methodName, String methodDescriptor, MethodVisitor methodVisitor) {
        super(api, methodVisitor);
        this.methodName = methodName;
        this.methodDescriptor = methodDescriptor;
        this.access = access;
    }

    @Override
    public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
        if (!codeHaveBeenVisited) {
            Type t = Type.getType(descriptor);
            addLog = LOG_ANNOTATION.equals(t.getClassName());
        }
        return super.visitAnnotation(descriptor, visible);
    }

    @Override
    public void visitCode() {
        super.visitCode();
        codeHaveBeenVisited = true;
        if (addLog) {
            addLog();
        }
    }

    private void addLog() {
        Type[] types = Type.getArgumentTypes(methodDescriptor);
        if (types.length == 0) {
            super.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
            super.visitLdcInsn("executed method: " + methodName + " ()");
            super.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);
        } else {
            super.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
            super.visitTypeInsn(Opcodes.NEW, "java/lang/StringBuilder");
            super.visitInsn(Opcodes.DUP);
            super.visitMethodInsn(Opcodes.INVOKESPECIAL, "java/lang/StringBuilder", "<init>", "()V", false);
            addStringBuilderFromStringConst("executed method: " + methodName + " (");
            int idx = Modifier.isStatic(access) ? 0 : 1;
            for (int i = 0; i < types.length; i++) {
                Type currentType = types[i];
                if (Type.SHORT_TYPE.equals(currentType) || Type.BYTE_TYPE.equals(currentType)) {
                    currentType = Type.INT_TYPE;
                }
                addStringBuilderFromStringConst("par" + (i + 1) + ": ");
                addStringBuilderFromVariable(idx, currentType);
                idx += currentType.getSize();
                if (i < (types.length - 1)) {
                    addStringBuilderFromStringConst(", ");
                }
            }
            addStringBuilderFromStringConst(")");
            super.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/Object;)V", false);
        }
    }

    private void addStringBuilderFromStringConst(String c) {
        super.visitLdcInsn(c);
        super.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
    }

    private void addStringBuilderFromVariable(int index, Type variableType) {
        super.visitVarInsn(variableType.getOpcode(Opcodes.ILOAD), index);
        super.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/StringBuilder", "append", Type.getMethodDescriptor(TYPE_STRING_BUILDER, variableType), false);
    }
}
