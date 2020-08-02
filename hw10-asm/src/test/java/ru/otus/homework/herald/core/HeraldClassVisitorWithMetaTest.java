package ru.otus.homework.herald.core;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import ru.otus.homework.herald.core.mock.ClassWithLogsMethods;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.reflect.Method;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.objectweb.asm.Opcodes.ASM8;

class HeraldClassVisitorWithMetaTest {

    private PrintStream originalSystemOut;
    private ByteArrayOutputStream systemOutContent;
    private Class<?> classForTest;
    private Object object;

    @BeforeEach
    void before() throws Exception {
        originalSystemOut = System.out;
        systemOutContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(systemOutContent));

        HeraldMeta heraldMeta0 = HeraldMeta.builder()
                .access(1)
                .methodName("testWithString")
                .methodDescriptor("(Ljava/lang/String;)V")
                .localVariable(LocalVariableMeta.builder()
                        .name("Eny Text For Name")
                        .descriptor("Ljava/lang/String;")
                        .index(1)
                        .build())
                .build();
        HeraldMeta heraldMeta1 = HeraldMeta.builder()
                .access(9)
                .methodName("testWithPrimitiveTypeAndObjectStatic")
                .methodDescriptor("(BSIJFDZCLjava/lang/Object;Ljava/lang/String;)V")
                .localVariable(LocalVariableMeta.builder()
                        .name("B").descriptor("B").index(0).build())
                .localVariable(LocalVariableMeta.builder()
                        .name("S").descriptor("S").index(1).build())
                .localVariable(LocalVariableMeta.builder()
                        .name("I").descriptor("I").index(2).build())
                .localVariable(LocalVariableMeta.builder()
                        .name("J").descriptor("J").index(3).build())
                .localVariable(LocalVariableMeta.builder()
                        .name("F").descriptor("F").index(5).build())
                .localVariable(LocalVariableMeta.builder()
                        .name("D").descriptor("D").index(6).build())
                .localVariable(LocalVariableMeta.builder()
                        .name("Z").descriptor("Z").index(8).build())
                .localVariable(LocalVariableMeta.builder()
                        .name("L0").descriptor("Ljava/lang/Object;").index(10).build())
                .localVariable(LocalVariableMeta.builder()
                        .name("L1").descriptor("Ljava/lang/String;").index(11).build())
                .build();

        ClassWriter wr = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
        ClassVisitor herald = new HeraldClassVisitor(ASM8, wr, Arrays.asList(heraldMeta0, heraldMeta1));
        ClassReader reader = new ClassReader(ClassWithLogsMethods.class.getCanonicalName());
        reader.accept(herald, ClassReader.EXPAND_FRAMES);

        ClassLoaderForTest classLoader = new ClassLoaderForTest();
        classForTest = classLoader.defineClass(ClassWithLogsMethods.class.getCanonicalName(), wr.toByteArray());
        object = classForTest.getConstructor().newInstance();
        assertNotNull(object);
    }

    @AfterEach
    void after() {
        System.setOut(originalSystemOut);
    }

    @Test
    void testWithString() throws Exception {
        Method m = classForTest.getMethod("testWithString", String.class);
        assertNotNull(m);
        m.invoke(object, "Example");
        assertEquals("executed method: testWithString (Eny Text For Name: Example)",
                systemOutContent.toString().trim());
    }

    @Test
    void testWithPrimitiveTypeAndObjectStatic() throws Exception {
        Method m0 = classForTest.getMethod("testWithPrimitiveTypeAndObjectStatic", HeraldClassVisitorTest.classPrimitiveTypeAndObjects);
        assertNotNull(m0);
        m0.invoke(null, HeraldClassVisitorTest.dataPrimitiveTypeAndObjects);
        assertEquals("executed method: testWithPrimitiveTypeAndObjectStatic (B: 120, S: 44, I: 122232, J: 7435837423, F: 343242.34, D: 8888888.222222, Z: false, par8: y, L0: [A, B], L1: qwerty)",
                systemOutContent.toString().trim());
    }
}