package ru.otus.homework.herald.core;

import org.junit.jupiter.api.Test;
import org.objectweb.asm.ClassReader;
import ru.otus.homework.herald.core.mock.ClassWithLogsMethods;

import java.util.Collection;
import java.util.Iterator;

import static org.junit.jupiter.api.Assertions.*;
import static org.objectweb.asm.Opcodes.ASM8;

class SearcherHeraldClassVisitorTest {

    @Test
    void testSearcher() throws Exception {
        SearcherHeraldClassVisitor searcher = new SearcherHeraldClassVisitor(ASM8);
        ClassReader reader = new ClassReader(ClassWithLogsMethods.class.getCanonicalName());
        reader.accept(searcher, ClassReader.EXPAND_FRAMES);
        Collection<HeraldMeta> heralds = searcher.getHeralds();
        assertEquals(16, heralds.size());
        boolean testWithoutParameters = false;
        boolean testWithPrimitiveTypeAndObjectPrivate = false;
        for (HeraldMeta herald : heralds) {
            switch (herald.getMethodName()) {
                case "testWithoutParameters":
                    testWithoutParameters = true;
                    assertEquals(1, herald.getLocalVariables().size());
                    break;
                case "testWithPrimitiveTypeAndObjectPrivate":
                    testWithPrimitiveTypeAndObjectPrivate = true;
                    assertEquals(11, herald.getLocalVariables().size());
                    Iterator<LocalVariableMeta> it = herald.getLocalVariables().iterator();
                    it.hasNext();
                    LocalVariableMeta v = it.next();
                    assertEquals("this", v.getName());
                    assertEquals(0, v.getIndex());

                    v = it.next();
                    assertEquals("by", v.getName());
                    assertEquals("B", v.getDescriptor());
                    assertEquals(1, v.getIndex());

                    v = it.next();
                    assertEquals("sh", v.getName());
                    assertEquals("S", v.getDescriptor());
                    assertEquals(2, v.getIndex());

                    v = it.next();
                    assertEquals("i", v.getName());
                    assertEquals("I", v.getDescriptor());
                    assertEquals(3, v.getIndex());

                    v = it.next();
                    assertEquals("l", v.getName());
                    assertEquals("J", v.getDescriptor());
                    assertEquals(4, v.getIndex());

                    v = it.next();
                    assertEquals("f", v.getName());
                    assertEquals("F", v.getDescriptor());
                    assertEquals(6, v.getIndex());

                    v = it.next();
                    assertEquals("d", v.getName());
                    assertEquals("D", v.getDescriptor());
                    assertEquals(7, v.getIndex());

                    v = it.next();
                    assertEquals("bo", v.getName());
                    assertEquals("Z", v.getDescriptor());
                    assertEquals(9, v.getIndex());

                    v = it.next();
                    assertEquals("c", v.getName());
                    assertEquals("C", v.getDescriptor());
                    assertEquals(10, v.getIndex());

                    v = it.next();
                    assertEquals("o", v.getName());
                    assertEquals("Ljava/lang/Object;", v.getDescriptor());
                    assertEquals(11, v.getIndex());

                    v = it.next();
                    assertEquals("st", v.getName());
                    assertEquals("Ljava/lang/String;", v.getDescriptor());
                    assertEquals(12, v.getIndex());
                    break;
            }
        }
        assertTrue(testWithoutParameters);
        assertTrue(testWithPrimitiveTypeAndObjectPrivate);
    }
}