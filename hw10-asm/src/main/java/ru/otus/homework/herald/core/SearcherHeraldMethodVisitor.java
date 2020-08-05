package ru.otus.homework.herald.core;

import lombok.Getter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Собираем метаинформацию для логера.
 * Как только информация собрата вызывается событие eventHeraldMetaComplete
 * Оно может быть вызвано только из двух мест.
 * </p>
 * 1) В секции visitCode если данные собраны из visitParameter
 * </p>
 * 2) В секции visitEnd если данные собраны в visitLocalVariable, либо данные в visitParameter не полные, либо нет совсем информации о переменных
 * </p>
 * Вызова совсем не будет если тэга логер небыло.
 */
public class SearcherHeraldMethodVisitor extends HeraldDetectorMethodVisitor {

    private final Consumer<SearcherHeraldMethodVisitor> eventHeraldMetaComplete;
    private final String internalClassName;
    private HeraldMeta.HeraldMetaBuilder heraldMetaBuilder;
    private boolean visitCodeEvent = false;
    @Getter
    private List<String> parameters;
    @Getter
    private HeraldMeta heraldMeta;

    public SearcherHeraldMethodVisitor(int api, int access, String methodName, String methodDescriptor, String internalClassName, MethodVisitor methodVisitor, Consumer<SearcherHeraldMethodVisitor> eventHeraldMetaComplete) {
        super(api, access, methodName, methodDescriptor, methodVisitor);
        this.eventHeraldMetaComplete = eventHeraldMetaComplete;
        this.internalClassName = internalClassName;
    }

    @Override
    public void visitParameter(String name, int access) {
        super.visitParameter(name, access);
        if (parameters == null) {
            parameters = new ArrayList<>();
        }
        parameters.add(name);
    }

    @Override
    public void visitCode() {
        super.visitCode();
        if (isHerald()) {
            HeraldMeta.HeraldMetaBuilder hmb = HeraldMeta.builder()
                    .access(getAccess())
                    .methodName(getMethodName())
                    .methodDescriptor(getMethodDescriptor())
                    .comment(getComment())
                    .printProperty(isPrintProperty());
            Type[] types = Type.getArgumentTypes(getMethodDescriptor());
            if (types.length != 0 && (parameters == null || parameters.size() != types.length)) {
                // Забываем о параметрах, так как их количество не соответствует сигнатуре
                // надежнее смотреть в visitLocalVariable
                parameters = null;
                heraldMetaBuilder = hmb;
            } else {
                visitCodeEvent = true;
                int idxOnStack = Modifier.isStatic(getAccess()) ? 0 : 1;
                if (idxOnStack == 1) {
                    hmb.localVariable(
                            LocalVariableMeta.builder()
                                    .name("this")
                                    .descriptor(Type.getObjectType(internalClassName).getDescriptor())
                                    .signature(null)
                                    .index(0)
                                    .build());
                }
                for (int i = 0; i < types.length; i++) {
                    Type type = types[i];
                    hmb.localVariable(
                            LocalVariableMeta.builder()
                                    .name(parameters.get(i))
                                    .descriptor(type.getDescriptor())
                                    .signature(null)
                                    .index(idxOnStack)
                                    .build());
                    idxOnStack += type.getSize();
                }
                heraldMeta = hmb.build();
                eventHeraldMetaComplete.accept(this);
            }
        }
    }

    @Override
    public void visitLocalVariable(String name, String descriptor, String signature, Label start, Label end, int index) {
        super.visitLocalVariable(name, descriptor, signature, start, end, index);
        if (!visitCodeEvent && isHerald()) {
            heraldMetaBuilder.localVariable(
                    LocalVariableMeta.builder()
                            .name(name)
                            .descriptor(descriptor)
                            .signature(signature)
                            .index(index)
                            .build());
        }
    }

    @Override
    public void visitEnd() {
        if (!visitCodeEvent && isHerald()) {
            heraldMeta = heraldMetaBuilder.build();
            eventHeraldMetaComplete.accept(this);
        }
        super.visitEnd();
    }

    public boolean isHeraldMetaAvailable() {
        return heraldMeta != null;
    }

    /**
     * Определяем где сработало событие
     *
     * @return если true, то мы находимся в visitCode иначе false и мы в visitEnd
     */
    public boolean isVisitCodeEvent() {
        return visitCodeEvent;
    }
}
