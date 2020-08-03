package ru.otus.homework.herald.core;

import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HeraldInjectorParametersLogBySystemOutTest {

    private static final Type STRING_BUILDER = Type.getType(StringBuilder.class);
    /**
     * Первый тип что по факту в дискрипторе функции, второй тип во что он должен сконвертироватся для вызова append у STRING_BUILDER
     * Если второго типа нет, то значит использовать первый
     */
    private static final List<Type[]> TYPE_FOR_CHECK = List.of(
            new Type[]{Type.getType(String.class)},
            new Type[]{Type.getType(int.class)},
            new Type[]{Type.getType(long.class)},
            new Type[]{Type.getType(double.class)},
            new Type[]{Type.getType(Object.class)},
            new Type[]{Type.getType(char[].class)},
            new Type[]{Type.getType(boolean.class)},
            new Type[]{Type.getType(byte.class), Type.getType(int.class)},
            new Type[]{Type.getType(short.class), Type.getType(int.class)},
            new Type[]{Type.getType(char[][].class), Type.getType(Object.class)},
            new Type[]{Type.getType(Integer.class), Type.getType(Object.class)},
            new Type[]{Type.getType(int[].class), Type.getType(Object.class)},
            new Type[]{Type.getType(List.class), Type.getType(Object.class)},
            new Type[]{Type.getType(CharSequence.class), Type.getType(Object.class)}
    );

    @Mock
    private MethodVisitor mockMethodVisitor;

    private static Collection<Type> returnTypes() {
        return List.of(
                Type.VOID_TYPE,
                Type.BOOLEAN_TYPE,
                Type.LONG_TYPE,
                Type.getType(List.class),
                Type.getType(HeraldInjectorParametersLogBySystemOutTest.class),
                Type.getType(char[].class),
                Type.getType(int[][].class)
        );
    }

    @ParameterizedTest
    @MethodSource("returnTypes")
    void injectWithoutParameters(Type returnType) {
        HeraldInjectorParametersLogBySystemOut bySystemOut = new HeraldInjectorParametersLogBySystemOut();
        HeraldMeta meta = HeraldMeta.builder()
                .access(1)
                .methodName("test")
                .methodDescriptor(Type.getMethodDescriptor(returnType))
                .build();
        InOrder order = inOrder(mockMethodVisitor);
        bySystemOut.inject(meta, mockMethodVisitor);
        order.verify(mockMethodVisitor, times(1)).visitFieldInsn(anyInt(), any(), any(), any());
        order.verify(mockMethodVisitor, times(1)).visitLdcInsn(anyString());
        order.verify(mockMethodVisitor, times(1)).visitMethodInsn(anyInt(), any(), any(), eq("(Ljava/lang/String;)V"), eq(false));
        order.verifyNoMoreInteractions();
    }

    @ParameterizedTest
    @MethodSource("returnTypes")
    void injectWithOneParameters(Type returnType) {
        HeraldInjectorParametersLogBySystemOut bySystemOut = new HeraldInjectorParametersLogBySystemOut();
        HeraldMeta meta = HeraldMeta.builder()
                .access(1)
                .methodName("test")
                .methodDescriptor(Type.getMethodDescriptor(returnType, Type.getType(String.class)))
                .build();
        InOrder order = inOrder(mockMethodVisitor);
        bySystemOut.inject(meta, mockMethodVisitor);

        order.verify(mockMethodVisitor, times(1)).visitFieldInsn(anyInt(), any(), any(), any());

        checkInit(order);
        checkConst(order);
        checkConst(order);
        checkVar(order);
        checkConst(order);

        order.verify(mockMethodVisitor, times(1)).visitMethodInsn(anyInt(), any(), any(), eq("(Ljava/lang/Object;)V"), eq(false));
        order.verifyNoMoreInteractions();
    }

    @ParameterizedTest
    @MethodSource("returnTypes")
    void injectWithUnknownParameters(Type returnType) {
        HeraldInjectorParametersLogBySystemOut bySystemOut = new HeraldInjectorParametersLogBySystemOut();
        HeraldMeta meta = HeraldMeta.builder()
                .access(1)
                .methodName("test")
                .methodDescriptor(Type.getMethodDescriptor(returnType, Type.VOID_TYPE))
                .build();
        InOrder order = inOrder(mockMethodVisitor);
        bySystemOut.inject(meta, mockMethodVisitor);

        order.verify(mockMethodVisitor, times(1)).visitFieldInsn(anyInt(), any(), any(), any());

        checkInit(order);
        checkConst(order);
        checkConst(order);
        checkConst(order, "*unknown type*");
        checkConst(order);

        order.verify(mockMethodVisitor, times(1)).visitMethodInsn(anyInt(), any(), any(), eq("(Ljava/lang/Object;)V"), eq(false));
        order.verifyNoMoreInteractions();
    }

    @ParameterizedTest
    @MethodSource({"returnTypes"})
    void injectWithLotOfParameter(Type returnType) {

        HeraldMeta meta = HeraldMeta.builder()
                .access(1)
                .methodName("test")
                .methodDescriptor(Type.getMethodDescriptor(returnType,
                        TYPE_FOR_CHECK.stream().map(x -> x[0]).toArray(Type[]::new)))
                .build();

        InOrder order = inOrder(mockMethodVisitor);

        HeraldInjectorParametersLogBySystemOut bySystemOut = new HeraldInjectorParametersLogBySystemOut();
        bySystemOut.inject(meta, mockMethodVisitor);

        order.verify(mockMethodVisitor, times(1)).visitFieldInsn(anyInt(), any(), any(), any());
        checkInit(order);
        checkConst(order);
        for (int i = 0; i < TYPE_FOR_CHECK.size(); i++) {
            checkConst(order, "par" + (i + 1) + ": ");
            Type[] tp = TYPE_FOR_CHECK.get(i);
            checkVar(order, Type.getMethodDescriptor(STRING_BUILDER, tp[tp.length - 1]));
            checkConst(order);
        }
        order.verify(mockMethodVisitor, times(1)).visitMethodInsn(anyInt(), any(), any(), eq("(Ljava/lang/Object;)V"), eq(false));
        order.verifyNoMoreInteractions();
    }

    @ParameterizedTest
    @MethodSource({"returnTypes"})
    void injectWithLotOfParameterAndVariableMeta(Type returnType) {
        AtomicInteger counter = new AtomicInteger(1);
        AtomicInteger counterWithShift = new AtomicInteger(1);
        HeraldMeta meta = HeraldMeta.builder()
                .access(1)
                .methodName("test")
                .methodDescriptor(Type.getMethodDescriptor(returnType,
                        TYPE_FOR_CHECK.stream().map(x -> x[0]).toArray(Type[]::new)))
                .localVariables(TYPE_FOR_CHECK.stream().map(x -> LocalVariableMeta.builder()
                        .name("nameZZZy" + counter.getAndIncrement())
                        .index(counterWithShift.getAndAdd(x[0].getSize()))
                        .build()).collect(Collectors.toList()))
                .build();

        InOrder order = inOrder(mockMethodVisitor);

        HeraldInjectorParametersLogBySystemOut bySystemOut = new HeraldInjectorParametersLogBySystemOut();
        bySystemOut.inject(meta, mockMethodVisitor);

        order.verify(mockMethodVisitor, times(1)).visitFieldInsn(anyInt(), any(), any(), any());
        checkInit(order);
        checkConst(order);
        for (int i = 0; i < TYPE_FOR_CHECK.size(); i++) {
            checkConst(order, "nameZZZy" + (i + 1) + ": ");
            Type[] tp = TYPE_FOR_CHECK.get(i);
            checkVar(order, Type.getMethodDescriptor(STRING_BUILDER, tp[tp.length - 1]));
            checkConst(order);
        }
        order.verify(mockMethodVisitor, times(1)).visitMethodInsn(anyInt(), any(), any(), eq("(Ljava/lang/Object;)V"), eq(false));
        order.verifyNoMoreInteractions();
    }

    private void checkInit(InOrder order) {
        order.verify(mockMethodVisitor, times(1)).visitTypeInsn(anyInt(), any());
        order.verify(mockMethodVisitor, times(1)).visitInsn(anyInt());
        order.verify(mockMethodVisitor, times(1)).visitMethodInsn(anyInt(), any(), any(), any(), anyBoolean());
    }

    private void checkConst(InOrder order) {
        order.verify(mockMethodVisitor, times(1)).visitLdcInsn(anyString());
        order.verify(mockMethodVisitor, times(1)).visitMethodInsn(anyInt(), any(), eq("append"), any(), anyBoolean());
    }

    private void checkVar(InOrder order) {
        order.verify(mockMethodVisitor, times(1)).visitVarInsn(anyInt(), anyInt());
        order.verify(mockMethodVisitor, times(1)).visitMethodInsn(anyInt(), any(), eq("append"), any(), anyBoolean());
    }

    private void checkVar(InOrder order, String descriptor) {
        order.verify(mockMethodVisitor, times(1)).visitVarInsn(anyInt(), anyInt());
        order.verify(mockMethodVisitor, times(1)).visitMethodInsn(anyInt(), any(), eq("append"), eq(descriptor), anyBoolean());
    }

    private void checkConst(InOrder order, String cons) {
        order.verify(mockMethodVisitor, times(1)).visitLdcInsn(eq(cons));
        order.verify(mockMethodVisitor, times(1)).visitMethodInsn(anyInt(), any(), eq("append"), any(), anyBoolean());
    }
}