package ru.otus.homework.herald.core;

import org.objectweb.asm.Handle;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import java.lang.invoke.CallSite;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

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
            HeraldUtils.setupILoadIntoStack(heraldMeta.getAccess(), types, visitor);
            visitor.visitInvokeDynamicInsn(
                    MAKE_CONCAT_WITH_CONSTANTS,
                    Type.getMethodDescriptor(STRING, types),
                    HANDLE_FOR_MAKE_CONCAT_WITH_CONSTANTS,
                    createPattern(types, heraldMeta, methodText, heraldMeta.getComment()));
        }
        visitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);
    }

    private String createPattern(Type[] types, HeraldMeta heraldMeta, String prefix, String comment) {
        AtomicInteger idxOnStack = new AtomicInteger(Modifier.isStatic(heraldMeta.getAccess()) ? 0 : 1);
        AtomicInteger idx = new AtomicInteger(1);
        String body = Arrays.stream(types)
                .map(x -> HeraldUtils.resolveName(idx.getAndIncrement(), idxOnStack.getAndAdd(x.getSize()), heraldMeta) + ": \u0001")
                .collect(Collectors.joining(", "));
        return prefix + " (" + body + ")" + (comment != null ? " // " + comment : "");
    }
}
