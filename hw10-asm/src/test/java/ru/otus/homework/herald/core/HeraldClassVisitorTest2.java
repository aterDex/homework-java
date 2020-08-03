package ru.otus.homework.herald.core;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.util.TraceClassVisitor;
import ru.otus.homework.herald.core.other.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.objectweb.asm.Opcodes.ASM8;

class HeraldClassVisitorTest2 {

    @BeforeEach
    void before() throws Exception {
        TraceClassVisitor traceClassVisitor = new TraceClassVisitor(new PrintWriter(System.out));
        ClassReader reader = new ClassReader(ClassWithLogsMethods2.class.getCanonicalName());
        reader.accept(traceClassVisitor, ClassReader.EXPAND_FRAMES);

        ClassLoaderForTest classLoader = new ClassLoaderForTest();
    }


    @Test
    void testException() throws Exception {
    }
}