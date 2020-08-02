package ru.otus.homework.herald.core;

import org.objectweb.asm.*;

import java.util.Collection;
import java.util.Optional;

/**
 * Класс добавляет логирования к методам которые помечены @Log
 * Если указан мета информация heralds, то добавляем логирование только в те методы по которым эта мета информация есть.
 * Если мета информации нет, то проверяем все методы, именя переменных при логировании при этом не будут настоящими.
 */
public class HeraldClassVisitor extends ClassVisitor {

    private final Collection<HeraldMeta> heralds;

    public HeraldClassVisitor(int api, ClassVisitor classVisitor) {
        this(api, classVisitor, null);
    }

    public HeraldClassVisitor(int api, ClassVisitor classVisitor, Collection<HeraldMeta> heralds) {
        super(api, classVisitor);
        this.heralds = heralds;
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        MethodVisitor baseVisitor = super.visitMethod(access, name, descriptor, signature, exceptions);
        if (heralds == null) {
            return new HeraldMethodVisitor(
                    api, access, name, descriptor, null, baseVisitor);
        }
        return findMetaBy(access, name, descriptor)
                .map(meta -> (MethodVisitor) new HeraldMethodVisitor(api, access, name, descriptor, meta, baseVisitor))
                .orElse(baseVisitor);
    }

    private Optional<HeraldMeta> findMetaBy(int access, String name, String descriptor) {
        if (heralds == null || heralds.isEmpty()) return Optional.empty();
        return heralds.parallelStream()
                .filter(x -> x.getAccess() == access)
                .filter(x -> name.equals(x.getMethodName()))
                .filter(x -> descriptor.equals(x.getMethodDescriptor()))
                .findAny();
    }
}
