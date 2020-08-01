package ru.otus.homework.herald.core.mock;

import ru.otus.homework.herald.api.Log;

import java.lang.invoke.StringConcatFactory;

public class NormalClass {

    @Log
    public void testWithoutParameters() {
    }

    @Log
    public void testWithPrimitiveByte(byte b) {
    }

    @Log
    public void testWithPrimitiveInt(int i) {
    }

    @Log
    public void testWithPrimitiveShort(short s) {
    }

    @Log
    public void testWithPrimitiveLong(long l) {
    }

    @Log
    public void testWithPrimitiveFloat(float f) {
    }

    @Log
    public void testWithPrimitiveDouble(double d) {
    }

    @Log
    public void testWithPrimitiveBoolean(boolean b) {
    }

    @Log
    public void testWithPrimitiveChar(char c) {
    }

    @Log
    public void testWithObject(Object o) {
    }

    @Log
    public void testWithString(String o) {
    }

    @Log
    public void testWithPrimitiveTypeAndObject(byte by, short sh, int i, long l, float f, double d, boolean bo, char c, Object o, String st) {
    }

    @Log
    protected void testWithPrimitiveTypeAndObjectProtected(byte by, short sh, int i, long l, float f, double d, boolean bo, char c, Object o, String st) {
    }

    @Log
    private void testWithPrimitiveTypeAndObjectPrivate(byte by, short sh, int i, long l, float f, double d, boolean bo, char c, Object o, String st) {
    }

    @Log
    void testWithPrimitiveTypeAndObjectProtectedPackage(byte by, short sh, int i, long l, float f, double d, boolean bo, char c, Object o, String st) {
    }

    @Log
    public static void testWithPrimitiveTypeAndObjectStatic(byte by, short sh, int i, long l, float f, double d, boolean bo, char c, Object o, String st) {
    }
}
