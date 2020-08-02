package ru.otus.homework.herald.core;

import org.objectweb.asm.*;

import java.io.PrintStream;
import java.lang.reflect.Modifier;
import java.util.Optional;

import static org.objectweb.asm.Opcodes.*;

public class HeraldMethodVisitor extends HeraldDetectorMethodVisitor {

    public static final Type PRINT_STREAM = Type.getType(PrintStream.class);
    public static final Type STRING_BUILDER = Type.getType(StringBuilder.class);

    public static final String PRINT_STREAM_DESC = PRINT_STREAM.getDescriptor();
    public static final String PRINT_STREAM_INTERNAL_NAME = PRINT_STREAM.getInternalName();
    public static final String STRING_BUILDER_INTERNAL_NAME = STRING_BUILDER.getInternalName();

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
        String methodText = "executed method: " + methodName;

        super.visitFieldInsn(
                GETSTATIC,
                "java/lang/System",
                "out",
                PRINT_STREAM_DESC);

        if (types.length == 0) {
            super.visitLdcInsn(methodText + " ()");
        } else {
            super.visitTypeInsn(NEW, STRING_BUILDER_INTERNAL_NAME);
            super.visitInsn(DUP);
            super.visitMethodInsn(
                    INVOKESPECIAL,
                    STRING_BUILDER_INTERNAL_NAME,
                    "<init>",
                    "()V",
                    false);

            addStringBuilderFromStringConst(methodText + " (");
            int idxOnStack = Modifier.isStatic(access) ? 0 : 1;
            for (int i = 0; i < types.length; i++) {
                Type currentType = correctTypeForStringBuilder(types[i]);
                addStringBuilderFromStringConst(resolveName(i + 1, idxOnStack) + ": ");
                addStringBuilderFromVariable(idxOnStack, currentType);
                idxOnStack += currentType.getSize();
                if (i < (types.length - 1)) {
                    addStringBuilderFromStringConst(", ");
                }
            }
            addStringBuilderFromStringConst(")");
        }

        super.visitMethodInsn(
                INVOKEVIRTUAL,
                PRINT_STREAM_INTERNAL_NAME,
                "println",
                "(Ljava/lang/Object;)V",
                false);
    }

    private Type correctTypeForStringBuilder(Type type) {
        return Type.SHORT_TYPE.equals(type) || Type.BYTE_TYPE.equals(type) ? Type.INT_TYPE : type;
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
        super.visitMethodInsn(
                INVOKEVIRTUAL,
                STRING_BUILDER_INTERNAL_NAME,
                "append",
                "(Ljava/lang/String;)Ljava/lang/StringBuilder;",
                false);
    }

    private void addStringBuilderFromVariable(int index, Type variableType) {
        super.visitVarInsn(variableType.getOpcode(ILOAD), index);
        super.visitMethodInsn(
                INVOKEVIRTUAL,
                STRING_BUILDER_INTERNAL_NAME,
                "append",
                Type.getMethodDescriptor(STRING_BUILDER, variableType),
                false);
    }
}
