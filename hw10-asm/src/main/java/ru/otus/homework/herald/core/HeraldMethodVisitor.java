package ru.otus.homework.herald.core;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

import java.io.PrintStream;
import java.lang.reflect.Modifier;
import java.util.Optional;

import static org.objectweb.asm.Opcodes.*;

public class HeraldMethodVisitor extends HeraldDetectorMethodVisitor {

    public static final Type PRINT_STREAM = Type.getType(PrintStream.class);
    public static final Type STRING_BUILDER = Type.getType(StringBuilder.class);
    public static final Type OBJECT = Type.getType(Object.class);
    public static final Type STRING = Type.getType(String.class);

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
        Type[] types = Type.getArgumentTypes(getMethodDescriptor());
        String methodText = "executed method: " + getMethodName();
        String finalDescriptor;

        super.visitFieldInsn(GETSTATIC, "java/lang/System", "out", PRINT_STREAM_DESC);

        if (types.length == 0) {
            finalDescriptor = "(Ljava/lang/String;)V";
            super.visitLdcInsn(methodText + " ()");
        } else {
            finalDescriptor = "(Ljava/lang/Object;)V";
            initStringBuilder();
            addStringBuilderFromStringConst(methodText + " (");
            addStringBuilderParameters(types);
            addStringBuilderFromStringConst(")");
        }

        super.visitMethodInsn(INVOKEVIRTUAL, PRINT_STREAM_INTERNAL_NAME,
                "println", finalDescriptor, false);
    }

    private void addStringBuilderParameters(Type[] types) {
        int idxOnStack = Modifier.isStatic(getAccess()) ? 0 : 1;
        for (int i = 0; i < types.length; i++) {
            addStringBuilderFromStringConst(resolveName(i + 1, idxOnStack) + ": ");
            addStringBuilderFromVariable(idxOnStack, types[i]);
            idxOnStack += types[i].getSize();
            if (i < (types.length - 1)) {
                addStringBuilderFromStringConst(", ");
            }
        }
    }

    /**
     * Создаем StringBuilder в памяти, на вершине стэка оставляем ссылку на StringBuilder
     */
    private void initStringBuilder() {
        super.visitTypeInsn(NEW, STRING_BUILDER_INTERNAL_NAME);
        super.visitInsn(DUP);
        super.visitMethodInsn(
                INVOKESPECIAL,
                STRING_BUILDER_INTERNAL_NAME,
                "<init>",
                "()V",
                false);
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
        Type correctType = correctTypeForStringBuilder(variableType);
        if (correctType == null) {
            addStringBuilderFromStringConst("*unknown type*");
        } else {
            super.visitVarInsn(correctType.getOpcode(ILOAD), index);
            super.visitMethodInsn(
                    INVOKEVIRTUAL,
                    STRING_BUILDER_INTERNAL_NAME,
                    "append",
                    Type.getMethodDescriptor(STRING_BUILDER, correctType),
                    false);
        }
    }

    private Type correctTypeForStringBuilder(Type type) {
        switch (type.getSort()) {
            case Type.BYTE:
            case Type.SHORT:
                return Type.INT_TYPE;
            case Type.INT:
            case Type.CHAR:
            case Type.DOUBLE:
            case Type.FLOAT:
            case Type.LONG:
            case Type.BOOLEAN:
                return type;
            case Type.ARRAY:
            case Type.OBJECT:
                return correctObjectAndArrayTypeForStringBuilder(type);
            default:
                return null;
        }
    }

    private Type correctObjectAndArrayTypeForStringBuilder(Type type) {
        if (type.getSort() == Type.ARRAY) {
            if (type.getDimensions() == 1 && Type.CHAR_TYPE.equals(type.getElementType())) {
                return type;
            }
        } else if (OBJECT.equals(type) || STRING.equals(type)) {
            return type;
        }
        return OBJECT;
    }
}
