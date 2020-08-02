package ru.otus.homework.herald.core;

import org.objectweb.asm.*;

import java.io.PrintStream;
import java.lang.reflect.Modifier;
import java.util.Optional;

public class HeraldMethodVisitor extends HeraldDetectorMethodVisitor {

    public static final Type TYPE_STRING_BUILDER = Type.getType(StringBuilder.class);
    public static final Type TYPE_PRINT_STREAM = Type.getType(PrintStream.class);

    private final HeraldMeta heraldMeta;

    public HeraldMethodVisitor(int api, int access, String methodName, String methodDescriptor, HeraldMeta heraldMeta, MethodVisitor methodVisitor) {
        super(api, access, methodName, methodDescriptor, methodVisitor);
        this.heraldMeta = heraldMeta;
    }

    @Override
    public void visitCode() {
        super.visitCode();
        if (heraldMeta != null || isHerald()) {
            createLog();
        }
    }

    private void createLog() {
        Type[] types = Type.getArgumentTypes(methodDescriptor);
        super.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System", "out", TYPE_PRINT_STREAM.getDescriptor());
        if (types.length == 0) {
            super.visitLdcInsn("executed method: " + methodName + " ()");
        } else {
            super.visitTypeInsn(Opcodes.NEW, TYPE_STRING_BUILDER.getInternalName());
            super.visitInsn(Opcodes.DUP);
            super.visitMethodInsn(Opcodes.INVOKESPECIAL, TYPE_STRING_BUILDER.getInternalName(), "<init>", "()V", false);
            addStringBuilderFromStringConst("executed method: " + methodName + " (");
            int idxOnStack = Modifier.isStatic(access) ? 0 : 1;
            for (int i = 0; i < types.length; i++) {
                Type currentType = types[i];
                if (Type.SHORT_TYPE.equals(currentType) || Type.BYTE_TYPE.equals(currentType)) {
                    currentType = Type.INT_TYPE;
                }
                addStringBuilderFromStringConst(resolveName(i + 1, idxOnStack) + ": ");
                addStringBuilderFromVariable(idxOnStack, currentType);
                idxOnStack += currentType.getSize();
                if (i < (types.length - 1)) {
                    addStringBuilderFromStringConst(", ");
                }
            }
            addStringBuilderFromStringConst(")");
        }
        super.visitMethodInsn(Opcodes.INVOKEVIRTUAL, TYPE_PRINT_STREAM.getInternalName(), "println", "(Ljava/lang/Object;)V", false);
    }

    /**
     * Резолвим имя переменной
     *
     * @param idx        порядковый номер переменной начиная с 1
     * @param idxOnStack номер перменной в стэке
     * @return имя для переменной
     */
    private String resolveName(int idx, int idxOnStack) {
        if (heraldMeta != null && !heraldMeta.getLocalVariables().isEmpty()) {
            Optional<LocalVariableMeta> localVar = heraldMeta.getLocalVariables().parallelStream()
                    .filter(x -> x.getIndex() == idxOnStack)
                    .findAny();
            if (localVar.isPresent()) {
                return localVar.get().getName();
            }
        }
        return "par" + idx;
    }

    private void addStringBuilderFromStringConst(String c) {
        super.visitLdcInsn(c);
        super.visitMethodInsn(Opcodes.INVOKEVIRTUAL, TYPE_STRING_BUILDER.getInternalName(), "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
    }

    private void addStringBuilderFromVariable(int index, Type variableType) {
        super.visitVarInsn(variableType.getOpcode(Opcodes.ILOAD), index);
        super.visitMethodInsn(Opcodes.INVOKEVIRTUAL, TYPE_STRING_BUILDER.getInternalName(), "append", Type.getMethodDescriptor(TYPE_STRING_BUILDER, variableType), false);
    }
}
