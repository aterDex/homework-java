package ru.otus.homework.herald.core;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.util.TraceClassVisitor;
import ru.otus.homework.herald.api.Log;
import ru.otus.homework.herald.core.mock.NormalClass;

import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.objectweb.asm.Opcodes.ASM8;

class HeraldClassVisitorTest {

    private static final Class<?>[] classPrimitiveTypeAndObjects = new Class[]{byte.class, short.class, int.class, long.class, float.class, double.class, boolean.class, char.class, Object.class, String.class};
    private static final Object[] dataPrimitiveTypeAndObjects = new Object[]{(byte) 120, (short) 44, 122232, 7435837423L, 343242.342342f, 8888888.222222, false, 'y', Arrays.asList("A", "B"), "qwerty"};

    private Class<?> classForTest;
    private ClassLoaderForTest classLoader;
    private Object o;

    @BeforeEach
    void before() throws Exception {
        ClassWriter wr = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
//        TraceClassVisitor tracer = new TraceClassVisitor(wr, new PrintWriter(System.out));
        ClassVisitor herald = new HeraldClassVisitor(ASM8, wr);
        ClassReader reader = new ClassReader(NormalClass.class.getCanonicalName());
        reader.accept(herald, ClassReader.EXPAND_FRAMES);
        ClassLoaderForTest classLoader = new ClassLoaderForTest();
        classForTest = classLoader.defineClass(NormalClass.class.getCanonicalName(), wr.toByteArray());
        o = classForTest.getConstructor().newInstance();
        assertNotNull(o);
    }

    @Test
    void testWithoutParameters() throws Exception {
        Method m = classForTest.getMethod("testWithoutParameters");
        assertNotNull(m);
        m.invoke(o);
    }

    @Test
    public void testWithPrimitiveByte() throws Exception {
        Method m = classForTest.getMethod("testWithPrimitiveByte", byte.class);
        assertNotNull(m);
        m.invoke(o, (byte) 127);
    }

    @Test
    void testWithPrimitiveShort() throws Exception {
        Method m = classForTest.getMethod("testWithPrimitiveShort", short.class);
        assertNotNull(m);
        m.invoke(o, (short) 99);
    }

    @Test
    void testWithPrimitiveInt() throws Exception {
        Method m = classForTest.getMethod("testWithPrimitiveInt", int.class);
        assertNotNull(m);
        m.invoke(o, 10);
    }

    @Test
    void testWithPrimitiveLong() throws Exception {
        Method m = classForTest.getMethod("testWithPrimitiveLong", long.class);
        assertNotNull(m);
        m.invoke(o, 100L);
    }

    @Test
    void testWithPrimitiveFloat() throws Exception {
        Method m = classForTest.getMethod("testWithPrimitiveFloat", float.class);
        assertNotNull(m);
        m.invoke(o, 2.0f);
    }

    @Test
    void testWithPrimitiveDouble() throws Exception {
        Method m = classForTest.getMethod("testWithPrimitiveDouble", double.class);
        assertNotNull(m);
        m.invoke(o, 3.4);
    }

    @Test
    void testWithPrimitiveBoolean() throws Exception {
        Method m = classForTest.getMethod("testWithPrimitiveBoolean", boolean.class);
        assertNotNull(m);
        m.invoke(o, true);
    }

    @Test
    void testWithPrimitiveChar() throws Exception {
        Method m = classForTest.getMethod("testWithPrimitiveChar", char.class);
        assertNotNull(m);
        m.invoke(o, 't');
    }

    @Test
    public void testWithObject() throws Exception {
        Method m = classForTest.getMethod("testWithObject", Object.class);
        assertNotNull(m);
        m.invoke(o, Arrays.asList("A", "B"));
    }

    @Test
    public void testWithString() throws Exception {
        Method m = classForTest.getMethod("testWithString", String.class);
        assertNotNull(m);
        m.invoke(o, "Example");
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "testWithPrimitiveTypeAndObject",
            "testWithPrimitiveTypeAndObjectProtected",
            "testWithPrimitiveTypeAndObjectPrivate",
            "testWithPrimitiveTypeAndObjectProtectedPackage"
    })
    void testWithPrimitiveTypeAndObject(String method) throws Exception {
        Method m = classForTest.getDeclaredMethod(method, classPrimitiveTypeAndObjects);
        assertNotNull(m);
        m.setAccessible(true);
        m.invoke(o, dataPrimitiveTypeAndObjects);
    }

    @Test
    public void testWithPrimitiveTypeAndObjectStatic() throws Exception {
        Method m = classForTest.getDeclaredMethod("testWithPrimitiveTypeAndObjectStatic", classPrimitiveTypeAndObjects);
        assertNotNull(m);
        m.invoke(null, dataPrimitiveTypeAndObjects);
    }
}