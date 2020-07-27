package ru.otus.homework.hw06.test.secret;

import ru.otus.homework.provoker.api.*;

import java.util.Iterator;
import java.util.List;

/**
 * да, это конечно не тестирование (можно было и order для них реализовать),
 * но в целом чтоб показать механику атрибутов, и изолированности каждого теста...
 */
@Description("Let's sing")
public class TestLoremIpsum {

    private static Iterator<String> filler;

    @BeforeAll
    public static void beforeAll() {
        System.out.println("Consolidated choir by Test03");
        System.out.println("LOREM IPSUM");
        filler = List.of(
                "Lorem ipsum dolor sit amet,",
                "consectetur adipiscing elit,",
                "sed do eiusmod tempor incididunt",
                "labore et dolore magna aliqua.",
                "Ut enim ad minim veniam,",
                "quis nostrud exercitation ullamco",
                "laboris nisi ut aliquip ex ea commodo consequat.",
                "Duis aute irure dolor in reprehenderit",
                "in voluptate velit esse cillum",
                "dolore eu fugiat nulla pariatur.",
                "Excepteur sint occaecat cupidatat non proident,",
                "sunt in culpa qui officia deserunt",
                "mollit anim id est laborum."
        ).iterator();
    }

    @AfterAll
    public static void afterAll() {
        System.out.println("In chorus: We have said all.");
    }

    @Before
    public void before() {
        System.out.printf("- %s say:", this).println();
    }

    private void nextSay(String method) {
        System.out.println();
        if (filler.hasNext()) {
            System.out.printf("\t%s\t\t%s", filler.next(), method).println();
        } else {
            System.out.println("Is silent in embarrassment");
        }
        System.out.println();
    }

    @Test
    public void test00() {
        nextSay("test00");
    }

    @Test
    public void test01() {
        nextSay("test01");
    }

    @Test
    public void test02() {
        nextSay("test02");
    }

    @Test
    public void test03() {
        nextSay("test03");
    }

    @Test
    public void test04() {
        nextSay("test04");
    }

    @Test
    public void test05() {
        nextSay("test05");
    }

    @Test
    public void test06() {
        nextSay("test06");
    }

    @Test
    public void test07() {
        nextSay("test07");
    }

    @Test
    public void test08() {
        nextSay("test08");
    }

    @Test
    public void test09() {
        nextSay("test09");
    }

    @Test
    public void test10() {
        nextSay("test10");
    }

    @Test
    public void test11() {
        nextSay("test11");
    }

    @Test
    public void test12() {
        nextSay("test12");
    }

    @Test
    @Description("He's constantly shy")
    public void test13() {
        nextSay("test13");
    }

    @After
    public void after() {
        System.out.printf("- %s have fallen silent!", this).println();
    }
}
