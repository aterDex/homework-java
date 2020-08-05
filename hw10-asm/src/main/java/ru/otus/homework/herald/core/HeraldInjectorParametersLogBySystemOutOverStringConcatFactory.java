package ru.otus.homework.herald.core;

import org.objectweb.asm.Handle;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import java.lang.invoke.CallSite;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Modifier;

public class HeraldInjectorParametersLogBySystemOutOverStringConcatFactory implements HeraldInjector {

    private static final Type STRING = Type.getType(String.class);
    private static final String MAKE_CONCAT_WITH_CONSTANTS = "makeConcatWithConstants";
    private static final Handle HANDLE_FOR_MAKE_CONCAT_WITH_CONSTANTS = new Handle(
            Opcodes.H_INVOKESTATIC,
            Type.getInternalName(java.lang.invoke.StringConcatFactory.class),
            MAKE_CONCAT_WITH_CONSTANTS,
            MethodType.methodType(CallSite.class, MethodHandles.Lookup.class, String.class, MethodType.class, String.class, Object[].class).toMethodDescriptorString(),
            false);

    @Override
    public void inject(HeraldMeta heraldMeta, MethodVisitor visitor) {
        String methodText = "executed method: " + heraldMeta.getMethodName();
        Type[] types = Type.getArgumentTypes(heraldMeta.getMethodDescriptor());

        visitor.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
        if (types.length == 0 || !heraldMeta.isPrintProperty()) {
            methodText += " ()";
            if (heraldMeta.getComment() != null) {
                methodText += " // " + heraldMeta.getComment();
            }
            visitor.visitLdcInsn(methodText);
        } else {
            int idxOnStack = Modifier.isStatic(heraldMeta.getAccess()) ? 0 : 1;
            for (int i = 0; i < types.length; i++) {
                visitor.visitVarInsn(types[i].getOpcode(Opcodes.ILOAD), idxOnStack);
                idxOnStack += types[i].getSize();
            }
            visitor.visitInvokeDynamicInsn(
                    MAKE_CONCAT_WITH_CONSTANTS,
                    Type.getMethodDescriptor(STRING, types),
                    HANDLE_FOR_MAKE_CONCAT_WITH_CONSTANTS,
                    createPattern(types, heraldMeta, methodText, heraldMeta.getComment()));
        }
        visitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);
    }

    private String resolveName(int idx, int idxOnStack, HeraldMeta heraldMeta) {
        if (heraldMeta != null) {
            return heraldMeta.getLocalVariables().stream()
                    .filter(x -> x.getIndex() == idxOnStack)
                    .findAny()
                    .map(x -> x.getName())
                    .orElse("par" + idx);
        }
        return "par" + idx;
    }

    private String createPattern(Type[] types, HeraldMeta heraldMeta, String prefix, String comment) {
        StringBuilder sb = new StringBuilder();
        sb.append(prefix + " (");
        int idxOnStack = Modifier.isStatic(heraldMeta.getAccess()) ? 0 : 1;
        for (int i = 1; i < types.length; i++) {
            sb.append(resolveName(i, idxOnStack, heraldMeta) + ": \u0001, ");
            idxOnStack += types[i - 1].getSize();
        }
        sb.append(resolveName(types.length, idxOnStack, heraldMeta) + ": \u0001)");
        if (comment != null) {
            sb.append(" // " + comment);
        }
        return sb.toString();
    }
}
