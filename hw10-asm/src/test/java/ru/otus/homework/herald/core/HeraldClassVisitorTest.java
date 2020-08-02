package ru.otus.homework.herald.core;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import ru.otus.homework.herald.core.mock.ClassWithLogsMethods;
import ru.otus.homework.herald.core.mock.ToStringException;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.objectweb.asm.Opcodes.ASM8;

class HeraldClassVisitorTest {

    public static final Class<?>[] classPrimitiveTypeAndObjects = new Class[]{byte.class, short.class, int.class, long.class, float.class, double.class, boolean.class, char.class, Object.class, String.class};
    public static final Object[] dataPrimitiveTypeAndObjects = new Object[]{(byte) 120, (short) 44, 122232, 7435837423L, 343242.342342f, 8888888.222222, false, 'y', Arrays.asList("A", "B"), "qwerty"};

    private PrintStream originalSystemOut;
    private ByteArrayOutputStream systemOutContent;
    private Class<?> classForTest;
    private ClassLoaderForTest classLoader;
    private Object o;

    @BeforeEach
    void before() throws Exception {
        originalSystemOut = System.out;
        systemOutContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(systemOutContent));

        ClassWriter wr = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
        ClassVisitor herald = new HeraldClassVisitor(ASM8, wr);
        ClassReader reader = new ClassReader(ClassWithLogsMethods.class.getCanonicalName());
        reader.accept(herald, ClassReader.EXPAND_FRAMES);

        ClassLoaderForTest classLoader = new ClassLoaderForTest();
        classForTest = classLoader.defineClass(ClassWithLogsMethods.class.getCanonicalName(), wr.toByteArray());
        o = classForTest.getConstructor().newInstance();
        assertNotNull(o);
    }

    @AfterEach
    void after() {
        System.setOut(originalSystemOut);
    }

    @Test
    void testWithoutParameters() throws Exception {
        Method m = classForTest.getMethod("testWithoutParameters");
        assertNotNull(m);
        m.invoke(o);
        assertEquals("executed method: testWithoutParameters ()", systemOutContent.toString().trim());
    }

    @Test
    void testWithPrimitiveByte() throws Exception {
        Method m = classForTest.getMethod("testWithPrimitiveByte", byte.class);
        assertNotNull(m);
        m.invoke(o, (byte) 127);
        assertEquals("executed method: testWithPrimitiveByte (par1: 127)", systemOutContent.toString().trim());
    }

    @Test
    void testWithPrimitiveShort() throws Exception {
        Method m = classForTest.getMethod("testWithPrimitiveShort", short.class);
        assertNotNull(m);
        m.invoke(o, (short) 99);
        assertEquals("executed method: testWithPrimitiveShort (par1: 99)", systemOutContent.toString().trim());
    }

    @Test
    void testWithPrimitiveInt() throws Exception {
        Method m = classForTest.getMethod("testWithPrimitiveInt", int.class);
        assertNotNull(m);
        m.invoke(o, 10);
        assertEquals("executed method: testWithPrimitiveInt (par1: 10)", systemOutContent.toString().trim());
    }

    @Test
    void testWithPrimitiveLong() throws Exception {
        Method m = classForTest.getMethod("testWithPrimitiveLong", long.class);
        assertNotNull(m);
        m.invoke(o, 100L);
        assertEquals("executed method: testWithPrimitiveLong (par1: 100)", systemOutContent.toString().trim());
    }

    @Test
    void testWithPrimitiveFloat() throws Exception {
        Method m = classForTest.getMethod("testWithPrimitiveFloat", float.class);
        assertNotNull(m);
        m.invoke(o, 2.0f);
        assertEquals("executed method: testWithPrimitiveFloat (par1: 2.0)", systemOutContent.toString().trim());
    }

    @Test
    void testWithPrimitiveDouble() throws Exception {
        Method m = classForTest.getMethod("testWithPrimitiveDouble", double.class);
        assertNotNull(m);
        m.invoke(o, 3.4);
        assertEquals("executed method: testWithPrimitiveDouble (par1: 3.4)", systemOutContent.toString().trim());
    }

    @Test
    void testWithPrimitiveBoolean() throws Exception {
        Method m = classForTest.getMethod("testWithPrimitiveBoolean", boolean.class);
        assertNotNull(m);
        m.invoke(o, true);
        assertEquals("executed method: testWithPrimitiveBoolean (par1: true)", systemOutContent.toString().trim());
    }

    @Test
    void testWithPrimitiveChar() throws Exception {
        Method m = classForTest.getMethod("testWithPrimitiveChar", char.class);
        assertNotNull(m);
        m.invoke(o, 't');
        assertEquals("executed method: testWithPrimitiveChar (par1: t)", systemOutContent.toString().trim());
    }

    @Test
    void testWithObject() throws Exception {
        Method m = classForTest.getMethod("testWithObject", Object.class);
        assertNotNull(m);
        m.invoke(o, Arrays.asList("A", "B"));
        assertEquals("executed method: testWithObject (par1: [A, B])", systemOutContent.toString().trim());
    }

    @Test
    void testWithString() throws Exception {
        Method m = classForTest.getMethod("testWithString", String.class);
        assertNotNull(m);
        m.invoke(o, "Example");
        assertEquals("executed method: testWithString (par1: Example)", systemOutContent.toString().trim());
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
        assertEquals("executed method: " + method + " (par1: 120, par2: 44, par3: 122232, par4: 7435837423, par5: 343242.34, par6: 8888888.222222, par7: false, par8: y, par9: [A, B], par10: qwerty)", systemOutContent.toString().trim());
    }

    @Test
    void testWithPrimitiveTypeAndObjectStatic() throws Exception {
        Method m = classForTest.getDeclaredMethod("testWithPrimitiveTypeAndObjectStatic", classPrimitiveTypeAndObjects);
        assertNotNull(m);
        m.invoke(null, dataPrimitiveTypeAndObjects);
        assertEquals("executed method: testWithPrimitiveTypeAndObjectStatic (par1: 120, par2: 44, par3: 122232, par4: 7435837423, par5: 343242.34, par6: 8888888.222222, par7: false, par8: y, par9: [A, B], par10: qwerty)", systemOutContent.toString().trim());
    }

    @Test
    void testException() throws Exception {
        Method m = classForTest.getDeclaredMethod("testWithObject", Object.class);
        assertNotNull(m);
        assertThrows(InvocationTargetException.class, () -> m.invoke(o, new ToStringException()));
        try {
            m.invoke(o, new ToStringException());
        } catch (InvocationTargetException ite) {
            assertEquals(RuntimeException.class, ite.getCause().getClass());
        }
    }
}