package ru.otus.homework.herald.core;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import ru.otus.homework.herald.core.other.BoxForParameters;
import ru.otus.homework.herald.core.other.ClassLoaderForTest;
import ru.otus.homework.herald.core.other.ClassWithLogsMethods;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.objectweb.asm.Opcodes.ASM8;

class HeraldClassVisitorInlineWithMetaTest {

    private PrintStream originalSystemOut;
    private ByteArrayOutputStream systemOutContent;
    private Class<?> classForTest;
    private Object targetObject;

    @BeforeEach
    void before() throws Exception {
        originalSystemOut = System.out;
        systemOutContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(systemOutContent));

        HeraldMeta heraldMeta0 = HeraldMeta.builder()
                .access(1)
                .methodName("testWithString")
                .methodDescriptor("(Ljava/lang/String;)V")
                .printProperty(true)
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
                .printProperty(true)
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
        ClassVisitor herald = new HeraldClassVisitorInline(ASM8, wr, Arrays.asList(heraldMeta0, heraldMeta1));
        ClassReader reader = new ClassReader(ClassWithLogsMethods.class.getCanonicalName());
        reader.accept(herald, ClassReader.EXPAND_FRAMES);

        ClassLoaderForTest classLoader = new ClassLoaderForTest();
        classForTest = classLoader.defineClass(ClassWithLogsMethods.class.getCanonicalName(), wr.toByteArray());
        targetObject = classForTest.getConstructor().newInstance();
        assertNotNull(targetObject);
    }

    @AfterEach
    void after() {
        System.setOut(originalSystemOut);
    }

    @ParameterizedTest
    @MethodSource("provideBoxForParameters")
    void testMethod(BoxForParameters box) throws Exception {
        Method m = classForTest.getDeclaredMethod(box.getMethod(), box.getTypes());
        assertNotNull(m);
        m.setAccessible(true);
        m.invoke(box.isTarget() ? targetObject : null, box.getData());
        assertEquals(box.getResult(), systemOutContent.toString().trim());
    }

    private static Collection<BoxForParameters> provideBoxForParameters() {
        return List.of(
                BoxForParameters.builder().target(true).method("testWithString")
                        .types(new Class<?>[]{String.class})
                        .data(new Object[]{"Example"})
                        .result("executed method: testWithString (Eny Text For Name: Example)")
                        .build(),
                BoxForParameters.builder().target(false).method("testWithPrimitiveTypeAndObjectStatic")
                        .types(HeraldClassVisitorInlineTest.CLASS_PRIMITIVE_TYPE_AND_OBJECTS)
                        .data(HeraldClassVisitorInlineTest.DATA_PRIMITIVE_TYPE_AND_OBJECTS)
                        .result("executed method: testWithPrimitiveTypeAndObjectStatic (B: 120, S: 44, I: 122232, J: 7435837423, F: 343242.34, D: 8888888.222222, Z: false, par8: y, L0: [A, B], L1: qwerty)")
                        .build());
    }
}