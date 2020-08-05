package ru.otus.homework.herald.core;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

import java.io.PrintStream;
import java.lang.reflect.Modifier;
import java.util.Optional;

import static org.objectweb.asm.Opcodes.*;

public class HeraldInjectorParametersLogBySystemOut implements HeraldInjector {

    public static final Type PRINT_STREAM = Type.getType(PrintStream.class);
    public static final Type STRING_BUILDER = Type.getType(StringBuilder.class);
    public static final Type OBJECT = Type.getType(Object.class);
    public static final Type STRING = Type.getType(String.class);
    public static final Type ARRAY_CHAR = Type.getType(char[].class);

    public static final String PRINT_STREAM_DESC = PRINT_STREAM.getDescriptor();
    public static final String PRINT_STREAM_INTERNAL_NAME = PRINT_STREAM.getInternalName();
    public static final String STRING_BUILDER_INTERNAL_NAME = STRING_BUILDER.getInternalName();

    @Override
    public void inject(HeraldMeta heraldMeta, MethodVisitor visitor) {
        Type[] types = Type.getArgumentTypes(heraldMeta.getMethodDescriptor());
        String methodText = "executed method: " + heraldMeta.getMethodName();
        boolean haveComment = heraldMeta.getComment() != null;
        String finalDescriptor;

        visitor.visitFieldInsn(GETSTATIC, "java/lang/System", "out", PRINT_STREAM_DESC);
        if (haveComment) visitor.visitInsn(DUP);

        if (!heraldMeta.isPrintProperty() || types.length == 0) {
            finalDescriptor = "(Ljava/lang/String;)V";
            visitor.visitLdcInsn(methodText + " ()");
        } else {
            finalDescriptor = "(Ljava/lang/Object;)V";
            initStringBuilder(visitor);
            addStringBuilderFromStringConst(methodText + " (", visitor);
            addStringBuilderParameters(types, heraldMeta, visitor);
            addStringBuilderFromStringConst(")", visitor);
        }

        if (haveComment) {
            visitor.visitMethodInsn(INVOKEVIRTUAL, PRINT_STREAM_INTERNAL_NAME,
                    "print", finalDescriptor, false);
            visitor.visitLdcInsn(" // " + heraldMeta.getComment());
            finalDescriptor = "(Ljava/lang/String;)V";
        }

        visitor.visitMethodInsn(INVOKEVIRTUAL, PRINT_STREAM_INTERNAL_NAME,
                "println", finalDescriptor, false);
    }

    private void addStringBuilderParameters(Type[] types, HeraldMeta heraldMeta, MethodVisitor visitor) {
        int idxOnStack = Modifier.isStatic(heraldMeta.getAccess()) ? 0 : 1;
        for (int i = 0; i < types.length; i++) {
            addStringBuilderFromStringConst(resolveName(i + 1, idxOnStack, heraldMeta) + ": ", visitor);
            addStringBuilderFromVariable(idxOnStack, types[i], visitor);
            idxOnStack += types[i].getSize();
            if (i < (types.length - 1)) {
                addStringBuilderFromStringConst(", ", visitor);
            }
        }
    }

    /**
     * Создаем StringBuilder в памяти, на вершине стэка оставляем ссылку на StringBuilder
     *
     * @param visitor visitor для записи
     */
    private void initStringBuilder(MethodVisitor visitor) {
        visitor.visitTypeInsn(NEW, STRING_BUILDER_INTERNAL_NAME);
        visitor.visitInsn(DUP);
        visitor.visitMethodInsn(
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
     * @param heraldMeta метаинформация по колонкам
     * @return имя для переменной
     */
    private String resolveName(int idx, int idxOnStack, HeraldMeta heraldMeta) {
        if (heraldMeta != null && !heraldMeta.getLocalVariables().isEmpty()) {
            Optional<LocalVariableMeta> localVar = heraldMeta.getLocalVariables().stream()
                    .filter(x -> x.getIndex() == idxOnStack)
                    .findAny();
            if (localVar.isPresent()) {
                return localVar.get().getName();
            }
        }
        return "par" + idx;
    }

    private void addStringBuilderFromStringConst(String text, MethodVisitor visitor) {
        visitor.visitLdcInsn(text);
        visitor.visitMethodInsn(
                INVOKEVIRTUAL,
                STRING_BUILDER_INTERNAL_NAME,
                "append",
                "(Ljava/lang/String;)Ljava/lang/StringBuilder;",
                false);
    }

    private void addStringBuilderFromVariable(int index, Type variableType, MethodVisitor visitor) {
        Type correctType = correctTypeForStringBuilder(variableType);
        if (correctType == null) {
            addStringBuilderFromStringConst("*unknown type*", visitor);
        } else {
            visitor.visitVarInsn(correctType.getOpcode(ILOAD), index);
            visitor.visitMethodInsn(
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
                if (ARRAY_CHAR.equals(type) || STRING.equals(type)) return type;
                return OBJECT;
            default:
                return null;
        }
    }
}
