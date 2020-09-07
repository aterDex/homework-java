package ru.otus.homework.herald.core;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.util.Collection;

/**
 * Добавляем в методы помечанные аннотацией Log, вывод параметров запуска.
 * Класс может в двух режимах resolveParameterName (true/false).
 * <p>
 * Режим по умолчанию resolveParameterName = false.
 * В этом режиме мы проходим по классу только раз, и если атрибут есть у метода
 * то вставляем в начало метода вывод параметров, при этом имеена параметров будут parN где N порядковый номер параметра.
 * <p>
 * В режиме resolveParameterName = true, мы проходим по классу дважды,
 * первый раз собираем информацию о аннатированных методах в частности именна переменных (если класс откомплирован с соответствующим ключом),
 * если для переменной не найденна информации о ее имени то будет использован parN как в преведущем режиме.
 * При втором проходе мы используем собранную информацию для формирования вывод у нужных методов.
 */
public class HeraldClassTransformerInline implements ClassFileTransformer {

    private static final int API = Opcodes.ASM8;
    private static final int READ_FLAGS = ClassReader.EXPAND_FRAMES;
    private static final int WRITE_FLAGS = ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES;

    private boolean resolveParameterName = false;

    public HeraldClassTransformerInline() {
    }

    public HeraldClassTransformerInline(boolean resolveParameterName) {
        this.resolveParameterName = resolveParameterName;
    }

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classFileBuffer) throws IllegalClassFormatException {
        try {
            Collection<HeraldMeta> meta = null;
            if (resolveParameterName) {
                meta = resolveMeta(classFileBuffer);
                if (meta.isEmpty()) return classFileBuffer;
            }

            ClassReader reader = new ClassReader(classFileBuffer);
            ClassWriter writer = new ClassWriter(WRITE_FLAGS);
            ClassVisitor visitor = new HeraldClassVisitorInline(API, writer, meta);
            reader.accept(visitor, READ_FLAGS);
            return writer.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
            return classFileBuffer;
        }
    }

    private Collection<HeraldMeta> resolveMeta(byte[] classFileBuffer) {
        ClassReader reader = new ClassReader(classFileBuffer);
        SearcherHeraldClassVisitor visitor = new SearcherHeraldClassVisitor(API);
        reader.accept(visitor, READ_FLAGS);
        return visitor.getHeralds();
    }

    public boolean isResolveParameterName() {
        return resolveParameterName;
    }

    public void setResolveParameterName(boolean resolveParameterName) {
        this.resolveParameterName = resolveParameterName;
    }
}
