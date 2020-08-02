package ru.otus.homework.herald.core;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class SearcherHeraldClassVisitor extends ClassVisitor {

    private List<HeraldMeta> herald = new ArrayList<>();

    public SearcherHeraldClassVisitor(int api) {
        super(api);
    }

    public SearcherHeraldClassVisitor(int api, ClassVisitor classVisitor) {
        super(api, classVisitor);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        return new SearcherHeraldMethodVisitor(api, access, name, descriptor, super.visitMethod(access, name, descriptor, signature, exceptions), herald::add);
    }

    public Collection<HeraldMeta> getHeralds() {
        return herald;
    }
}
