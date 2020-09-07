package ru.otus.homework.herald.core;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import java.lang.reflect.Modifier;
import java.util.stream.Collectors;

/**
 * Класс добавляет логирования к методам которые помечены @Log.
 * Добавление происходит черз создание лог метода, или inline (через вставку в тот же метод)
 * если получилось прочитать переменные из visitParam
 */
public class HeraldClassVisitorLogMethod extends ClassVisitor {

    private static final int ACCESS_FOR_LOG_METHOD = Opcodes.ACC_PRIVATE + Opcodes.ACC_STATIC + Opcodes.ACC_SYNTHETIC;

    private String internalClassName;
    private int logMethodCounter = 0;
    private HeraldInjector heraldInjector = new HeraldInjectorParametersLogBySystemOutOverStringConcatFactory();

    public HeraldClassVisitorLogMethod(int api, ClassVisitor classVisitor) {
        super(api, classVisitor);
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        internalClassName = name;
        super.visit(version, access, name, signature, superName, interfaces);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        return new SearcherHeraldMethodVisitor(api, access, name, descriptor,
                internalClassName,
                super.visitMethod(access, name, descriptor, signature, exceptions),
                this::eventHeraldMetaComplete) {

            @Override
            public void visitCode() {
                super.visitCode();
                // Если события в VisitCodeEvent не произошло, то его уже и не будет, поэтому логируем через метод
                if (isHerald() && !isVisitCodeEvent()) {
                    logMethodCounter++;
                    createInvokeLogMethod(getAccess(), getMethodName(), getMethodDescriptor(), this);
                }
            }
        };
    }

    private void eventHeraldMetaComplete(SearcherHeraldMethodVisitor visitor) {
        if (visitor.isVisitCodeEvent()) {
            heraldInjector.inject(visitor.getHeraldMeta(), visitor);
        } else {
            creteLogMethod(visitor.getHeraldMeta());
        }
    }

    private void createInvokeLogMethod(int access, String name, String descriptor, MethodVisitor visitor) {
        Type[] types = Type.getArgumentTypes(descriptor);
        HeraldUtils.setupILoadIntoStack(access, types, visitor);
        visitor.visitMethodInsn(Opcodes.INVOKESTATIC, internalClassName, createNameForLogMethod(name), createDescriptorForLogMethod(descriptor), false);
    }

    private void creteLogMethod(HeraldMeta heraldMeta) {
        MethodVisitor mv = super.visitMethod(ACCESS_FOR_LOG_METHOD,
                createNameForLogMethod(heraldMeta.getMethodName()),
                createDescriptorForLogMethod(heraldMeta.getMethodDescriptor()),
                null,
                null);
        HeraldMeta correctHeraldMeta;
        if (Modifier.isStatic(heraldMeta.getAccess()) || heraldMeta.getLocalVariables().isEmpty()) {
            correctHeraldMeta = heraldMeta.toBuilder().access(ACCESS_FOR_LOG_METHOD).build();
        } else {
            correctHeraldMeta = heraldMeta.toBuilder()
                    .access(ACCESS_FOR_LOG_METHOD)
                    .clearLocalVariables()
                    .localVariables(heraldMeta.getLocalVariables().stream()
                            .skip(1)
                            .map(x -> x.toBuilder().index(x.getIndex() - 1).build())
                            .collect(Collectors.toList()))
                    .build();
        }

        heraldInjector.inject(correctHeraldMeta, mv);
        mv.visitInsn(Opcodes.RETURN);
        mv.visitMaxs(0, 0);
        mv.visitEnd();
    }

    private String createNameForLogMethod(String methodName) {
        return methodName + "LogMethod" + logMethodCounter;
    }

    private String createDescriptorForLogMethod(String methodDescriptor) {
        return Type.getMethodDescriptor(Type.VOID_TYPE, Type.getArgumentTypes(methodDescriptor));
    }
}
