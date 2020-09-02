package ru.otus.homework.hson;

import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HsonTest {

    @Test
    void primitiveField() {
        Hson hson = new Hson();
        var expected = "{\"bt\":10,\"sh\":11,\"in\":12,\"ln\":13,\"db\":14.0323,\"fl\":11.010000228881836,\"bl\":true,\"ch\":\"T\",\"st\":\"str\"}";
        var result0 = hson.toJson(new Object() {
            byte bt = 10;
            short sh = 11;
            int in = 12;
            long ln = 13L;
            double db = 14.0323;
            float fl = 11.01f;
            boolean bl = true;
            char ch = 'T';
            String st = "str";
        });
        assertEquals(expected, result0);

        var result1 = hson.toJson(new Object() {
            Byte bt = 10;
            Short sh = 11;
            Integer in = 12;
            Long ln = 13L;
            Double db = 14.0323;
            Float fl = 11.01f;
            Boolean bl = true;
            Character ch = 'T';
            String st = "str";
        });

        assertEquals(expected, result1);

    }

    @Test
    void nullFields() {
        Hson hson = new Hson();
        var result = hson.toJson(new Object() {
            Object a;
            String b;
            Long c;
            Double d;
            int[] e;
            Collection f;
        });
        assertEquals("{\"a\":null,\"b\":null,\"c\":null,\"d\":null,\"e\":null,\"f\":null}", result);
    }

    @Test
    void collectionFields() {
        Hson hson = new Hson();
        var result = hson.toJson(new Object() {
            Collection<String> l0 = List.of();
            Collection<String> l1 = List.of("a");
            Collection<String> l5 = List.of("a", "b", "c", "d");
        });
        assertEquals("{\"l0\":[],\"l1\":[\"a\"],\"l5\":[\"a\",\"b\",\"c\",\"d\"]}", result);
    }

    @Test
    void collection2Fields() {
        Hson hson = new Hson();
        var result = hson.toJson(new Object() {
            Collection<Collection<String>> l0 = List.of(List.of("A", "B"), List.of("C", "D"), List.of());
        });
        assertEquals("{\"l0\":[[\"A\",\"B\"],[\"C\",\"D\"],[]]}", result);
    }

    @Test
    void arraysFields() {
        Hson hson = new Hson();
        var result = hson.toJson(new Object() {
            String[] a0 = new String[0];
            String[] a1 = new String[]{"a"};
            String[] a5 = new String[]{"a", "b", "c", "d"};
        });
        assertEquals("{\"a0\":[],\"a1\":[\"a\"],\"a5\":[\"a\",\"b\",\"c\",\"d\"]}", result);
    }

    @Test
    void arrays2Fields() {
        Hson hson = new Hson();
        var result = hson.toJson(new Object() {
            String[][] a0 = new String[0][0];
            String[][] a1 = new String[][]{{"a", "b", "c"}};
            String[][] a2 = new String[][]{{"a0", "b0", "c0"}, {"a1", "b1", "c1"}, {"a2", "b2", "c2"}};
            String[][][] a4 = new String[][][]{{{"a0"}, {"b0", "c0"}}, {{"a1", "b1"}, {"c1"}}, {{"a2"}, {"b2"}}};
        });
        assertEquals("{\"a0\":[],\"a1\":[[\"a\",\"b\",\"c\"]],\"a2\":[[\"a0\",\"b0\",\"c0\"],[\"a1\",\"b1\",\"c1\"],[\"a2\",\"b2\",\"c2\"]],\"a4\":[[[\"a0\"],[\"b0\",\"c0\"]],[[\"a1\",\"b1\"],[\"c1\"]],[[\"a2\"],[\"b2\"]]]}", result);
    }


    @Test
    void objectsFields() {
        Hson hson = new Hson();
        var result = hson.toJson(new Object() {
            Object object1 = new Object() {
                String a = "value a";
            };
            Object object2 = new Object() {
                String a = "value a";
                String b = "value b";
            };
            Object st = "String";
        });
        assertEquals("{\"object1\":{\"a\":\"value a\"},\"object2\":{\"a\":\"value a\",\"b\":\"value b\"},\"st\":\"String\"}", result);
    }
}