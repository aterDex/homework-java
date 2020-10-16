package ru.otus.homework.herald.core;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

import static org.objectweb.asm.Opcodes.ASM8;

/**
 * Добавляем в методы помечанные аннотацией Log, вывод параметров запуска.
 * Работает в один проход, через создание синтечиского метода через который логируем.
 * Например для метода
 *
 * <pre>
 *     {@code
 *     public void test(double lat, double lon) {
 *         // ...
 *     }
 *     }
 * </pre>
 * Делает в байт коде следующую пару.
 * <pre>
 * {@code
 *      public void test(double lat, double lon) {
 *          testLogMethod(lat, lon);
 *          // ...
 *      }
 *
 *      private static void testLogMethod(double var1, double var2) {
 *          System.out.println(...);
 *      }
 *  }
 *  </pre>
 */
public class HeraldClassTransformerLogMethod implements ClassFileTransformer {

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classFileBuffer) throws IllegalClassFormatException {
        try {
            ClassReader reader = new ClassReader(classFileBuffer);
            ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
            ClassVisitor visitor = new HeraldClassVisitorLogMethod(ASM8, writer);
            reader.accept(visitor, ClassReader.EXPAND_FRAMES);
            return writer.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
            return classFileBuffer;
        }
    }
}
