package ru.otus.homework.herald.core;

import lombok.Getter;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;

import java.util.ArrayList;
import java.util.List;

public class SearcherHeraldClassVisitor extends ClassVisitor {

    @Getter
    private final List<HeraldMeta> heralds = new ArrayList<>();

    private String internalClassName;

    public SearcherHeraldClassVisitor(int api) {
        super(api);
    }

    public SearcherHeraldClassVisitor(int api, ClassVisitor classVisitor) {
        super(api, classVisitor);
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        this.internalClassName = name;
        super.visit(version, access, name, signature, superName, interfaces);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        return new SearcherHeraldMethodVisitor(
                api,
                access,
                name,
                descriptor,
                internalClassName,
                super.visitMethod(access, name, descriptor, signature, exceptions),
                visitor -> heralds.add(visitor.getHeraldMeta()));
    }
}
