package ru.otus.homework.herald.core;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import ru.otus.homework.herald.core.other.BoxForParameters;
import ru.otus.homework.herald.core.other.ClassLoaderForTest;
import ru.otus.homework.herald.core.other.ClassWithLogsMethods;
import ru.otus.homework.herald.core.other.ToStringException;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.objectweb.asm.Opcodes.ASM8;

class HeraldClassVisitorTest {

    public static final Class<?>[] CLASS_PRIMITIVE_TYPE_AND_OBJECTS = new Class[]{byte.class, short.class, int.class, long.class, float.class, double.class, boolean.class, char.class, Object.class, String.class};
    public static final Object[] DATA_PRIMITIVE_TYPE_AND_OBJECTS = new Object[]{(byte) 120, (short) 44, 122232, 7435837423L, 343242.342342f, 8888888.222222, false, 'y', Arrays.asList("A", "B"), "qwerty"};

    private PrintStream originalSystemOut;
    private ByteArrayOutputStream systemOutContent;
    private Class<?> classForTest;
    private Object targetObject;

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
        targetObject = classForTest.getConstructor().newInstance();
        assertNotNull(targetObject);
    }

    @AfterEach
    void after() {
        System.setOut(originalSystemOut);
    }

    @Test
    void testException() throws Exception {
        Method m = classForTest.getDeclaredMethod("testWithObject", Object.class);
        assertNotNull(m);
        assertThrows(InvocationTargetException.class, () -> m.invoke(targetObject, new ToStringException()));
        try {
            m.invoke(targetObject, new ToStringException());
        } catch (InvocationTargetException ite) {
            assertEquals(RuntimeException.class, ite.getCause().getClass());
        }
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
                BoxForParameters.builder().target(true).method("testWithoutParameters")
                        .result("executed method: testWithoutParameters ()")
                        .build(),
                BoxForParameters.builder().target(true).method("testWithPrimitiveByte")
                        .types(new Class<?>[]{byte.class})
                        .data(new Object[]{(byte) 127})
                        .result("executed method: testWithPrimitiveByte (par1: 127)")
                        .build(),
                BoxForParameters.builder().target(true).method("testWithPrimitiveShort")
                        .types(new Class<?>[]{short.class})
                        .data(new Object[]{(short) 99})
                        .result("executed method: testWithPrimitiveShort (par1: 99)")
                        .build(),
                BoxForParameters.builder().target(true).method("testWithPrimitiveInt")
                        .types(new Class<?>[]{int.class})
                        .data(new Object[]{10})
                        .result("executed method: testWithPrimitiveInt (par1: 10)")
                        .build(),
                BoxForParameters.builder().target(true).method("testWithPrimitiveLong")
                        .types(new Class<?>[]{long.class})
                        .data(new Object[]{100L})
                        .result("executed method: testWithPrimitiveLong (par1: 100)")
                        .build(),
                BoxForParameters.builder().target(true).method("testWithPrimitiveFloat")
                        .types(new Class<?>[]{float.class})
                        .data(new Object[]{2.0f})
                        .result("executed method: testWithPrimitiveFloat (par1: 2.0)")
                        .build(),
                BoxForParameters.builder().target(true).method("testWithPrimitiveDouble")
                        .types(new Class<?>[]{double.class})
                        .data(new Object[]{3.4})
                        .result("executed method: testWithPrimitiveDouble (par1: 3.4)")
                        .build(),
                BoxForParameters.builder().target(true).method("testWithPrimitiveBoolean")
                        .types(new Class<?>[]{boolean.class})
                        .data(new Object[]{true})
                        .result("executed method: testWithPrimitiveBoolean (par1: true)")
                        .build(),
                BoxForParameters.builder().target(true).method("testWithPrimitiveChar")
                        .types(new Class<?>[]{char.class})
                        .data(new Object[]{'t'})
                        .result("executed method: testWithPrimitiveChar (par1: t)")
                        .build(),
                BoxForParameters.builder().target(true).method("testWithObject")
                        .types(new Class<?>[]{Object.class})
                        .data(new Object[]{Arrays.asList("A", "B")})
                        .result("executed method: testWithObject (par1: [A, B])")
                        .build(),
                BoxForParameters.builder().target(true).method("testWithString")
                        .types(new Class<?>[]{String.class})
                        .data(new Object[]{"Example"})
                        .result("executed method: testWithString (par1: Example)")
                        .build(),
                BoxForParameters.builder().target(true).method("testWithPrimitiveTypeAndObject")
                        .types(CLASS_PRIMITIVE_TYPE_AND_OBJECTS)
                        .data(DATA_PRIMITIVE_TYPE_AND_OBJECTS)
                        .result("executed method: testWithPrimitiveTypeAndObject (par1: 120, par2: 44, par3: 122232, par4: 7435837423, par5: 343242.34, par6: 8888888.222222, par7: false, par8: y, par9: [A, B], par10: qwerty)")
                        .build(),
                BoxForParameters.builder().target(true).method("testWithPrimitiveTypeAndObjectProtected")
                        .types(CLASS_PRIMITIVE_TYPE_AND_OBJECTS)
                        .data(DATA_PRIMITIVE_TYPE_AND_OBJECTS)
                        .result("executed method: testWithPrimitiveTypeAndObjectProtected (par1: 120, par2: 44, par3: 122232, par4: 7435837423, par5: 343242.34, par6: 8888888.222222, par7: false, par8: y, par9: [A, B], par10: qwerty)")
                        .build(),
                BoxForParameters.builder().target(true).method("testWithPrimitiveTypeAndObjectPrivate")
                        .types(CLASS_PRIMITIVE_TYPE_AND_OBJECTS)
                        .data(DATA_PRIMITIVE_TYPE_AND_OBJECTS)
                        .result("executed method: testWithPrimitiveTypeAndObjectPrivate (par1: 120, par2: 44, par3: 122232, par4: 7435837423, par5: 343242.34, par6: 8888888.222222, par7: false, par8: y, par9: [A, B], par10: qwerty)")
                        .build(),
                BoxForParameters.builder().target(true).method("testWithPrimitiveTypeAndObjectProtectedPackage")
                        .types(CLASS_PRIMITIVE_TYPE_AND_OBJECTS)
                        .data(DATA_PRIMITIVE_TYPE_AND_OBJECTS)
                        .result("executed method: testWithPrimitiveTypeAndObjectProtectedPackage (par1: 120, par2: 44, par3: 122232, par4: 7435837423, par5: 343242.34, par6: 8888888.222222, par7: false, par8: y, par9: [A, B], par10: qwerty)")
                        .build(),
                BoxForParameters.builder().target(false).method("testWithPrimitiveTypeAndObjectStatic")
                        .types(CLASS_PRIMITIVE_TYPE_AND_OBJECTS)
                        .data(DATA_PRIMITIVE_TYPE_AND_OBJECTS)
                        .result("executed method: testWithPrimitiveTypeAndObjectStatic (par1: 120, par2: 44, par3: 122232, par4: 7435837423, par5: 343242.34, par6: 8888888.222222, par7: false, par8: y, par9: [A, B], par10: qwerty)")
                        .build());
    }
}