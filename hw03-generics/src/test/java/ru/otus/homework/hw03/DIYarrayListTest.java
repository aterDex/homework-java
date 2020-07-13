package ru.otus.homework.hw03;

import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class DIYarrayListTest {

    public static final String FILLER = "filler";

    @Test
    void add() {
        String testElement = "test";
        DIYarrayList<String> list = new DIYarrayList<>();
        assertEquals(0, list.size());

        list.add(FILLER);
        assertEquals(1, list.size());

        for (int i = 0; i < 100000; i++) {
            list.add(FILLER);
        }
        assertEquals(100001, list.size());

        list.add(list.size() - 100, testElement);
        assertEquals(100002, list.size());
        assertEquals(testElement, list.get(list.size() - 101));
    }

    @Test
    void contains() {
        String elementForFind = "find";

        DIYarrayList<String> list = new DIYarrayList<>();
        for (int i = 0; i < 100000; i++) {
            list.add(FILLER);
        }
        assertFalse(list.contains(elementForFind));

        list.set(5000, elementForFind);
        assertTrue(list.contains(elementForFind));
    }

    @Test
    void toArray() {
        DIYarrayList<String> list = new DIYarrayList<>();
        assertEquals(0, list.toArray().length);

        for (int i = 0; i < 1000; i++) {
            list.add(FILLER + i);
        }
        Object[] array = list.toArray();
        assertEquals(1000, array.length);

        for (int i = 0; i < list.size(); i++) {
            assertEquals(list.get(i), array[i]);
        }

        assertThrows(UnsupportedOperationException.class, () -> list.toArray(new String[0]));
    }

    @Test
    void remove() {
        String elementForRemove = "test";
        List<String> listForCheck = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            listForCheck.add(FILLER + i);
        }

        DIYarrayList<String> list = new DIYarrayList<>();
        list.addAll(listForCheck);

        list.add(500, "indexRemove");
        assertEquals(1001, list.size());
        list.remove(500);
        assertEquals(1000, list.size());

        assertArrayEquals(listForCheck.toArray(), list.toArray());

        assertFalse(list.remove(null));
        assertFalse(list.remove(elementForRemove));

        list.add(500, elementForRemove);
        assertEquals(1001, list.size());
        assertTrue(list.remove(elementForRemove));
        assertEquals(1000, list.size());
        assertFalse(list.contains(elementForRemove));

        list.add(600, elementForRemove);
        list.add(610, elementForRemove);
        assertEquals(1002, list.size());

        assertTrue(list.remove(elementForRemove));
        assertEquals(1001, list.size());
        assertEquals(list.get(609), elementForRemove);

        assertTrue(list.remove(elementForRemove));
        assertFalse(list.remove(elementForRemove));

        assertArrayEquals(listForCheck.toArray(), list.toArray());

        assertThrows(IndexOutOfBoundsException.class, () -> list.remove(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> list.remove(list.size()));
    }

    @Test
    void containsAll() {
        List<String> subset = new ArrayList<>(10);
        for (int i = 0; i < 10; i++) {
            subset.add("element" + i);
        }

        DIYarrayList<String> list = new DIYarrayList<>();
        for (int i = 0; i < 1000; i++) {
            list.add(FILLER + i);
        }

        assertThrows(NullPointerException.class, () -> list.containsAll(null));
        assertTrue(list.containsAll(Collections.EMPTY_LIST));
        assertFalse(list.containsAll(subset));

        Random random = new Random(9648640);

        Iterator<String> subsetIt = subset.iterator();
        random.ints(subset.size(), 0, list.size()).forEach(x -> list.set(x, subsetIt.next()));
        assertTrue(list.containsAll(subset));

        subset.add("elementX");
        assertFalse(list.containsAll(subset));
    }

    @Test
    void addAll() {
        List<String> subset1 = new ArrayList<>(1000);
        for (int i = 0; i < 1000; i++) {
            subset1.add(FILLER + i);
        }

        DIYarrayList<String> list = new DIYarrayList<>();
        list.addAll(subset1);

        assertArrayEquals(subset1.toArray(), list.toArray());

        DIYarrayList<String> list2 = new DIYarrayList<>();
        list2.add(FILLER);

        list2.addAll(subset1);
        assertEquals(subset1.size() + 1, list2.size());
        for (int i = 0; i < subset1.size(); i++) {
            assertEquals(subset1.get(i), list2.get(i + 1));
        }

        List<String> subset2 = new ArrayList<>(10);
        for (int i = 0; i < 10; i++) {
            subset2.add(FILLER + "sub" + i);
        }

        int snapshotList2size = list2.size();
        list2.addAll(10, subset2);
        for (int i = 0; i < 10; i++) {
            assertEquals(subset2.get(i), list2.get(i + 10));
        }
        assertEquals(snapshotList2size + 10, list2.size());
        assertThrows(IndexOutOfBoundsException.class, () -> list2.addAll(-1, subset2));
        assertThrows(IndexOutOfBoundsException.class, () -> list2.addAll(list2.size(), subset2));
    }

    @Test
    void removeAll() {
        DIYarrayList<String> list = new DIYarrayList<>();
        Runnable invalidateList = () -> {
            list.clear();
            for (int i = 0; i < 10000; i++) {
                list.add(FILLER + i);
            }
        };
        invalidateList.run();
        int snapshotListSize = list.size();

        Random random = new Random(9648640);

        List<String> removeListIn = random.ints(10, 0, list.size()).mapToObj(x -> FILLER + x).collect(Collectors.toList());
        List<String> removeListOut = random.ints(10, list.size(), list.size() + 50).mapToObj(x -> FILLER + x).collect(Collectors.toList());

        List<String> removeListInOut = new ArrayList<>();
        removeListInOut.addAll(removeListIn.subList(0, 5));
        removeListInOut.addAll(removeListOut.subList(0, 5));

        assertTrue(list.containsAll(removeListIn));
        for (String elementOut : removeListOut) {
            assertFalse(list.contains(elementOut));
        }

        assertThrows(NullPointerException.class, () -> list.removeAll(null));

        assertFalse(list.removeAll(Collections.EMPTY_LIST));

        assertFalse(list.removeAll(removeListOut));
        assertEquals(snapshotListSize, list.size());

        assertTrue(list.containsAll(removeListIn));
        assertTrue(list.removeAll(removeListIn));
        assertEquals(snapshotListSize - 10, list.size());
        for (String elementOut : removeListIn) {
            assertFalse(list.contains(elementOut));
        }

        invalidateList.run();
        assertEquals(snapshotListSize, list.size());
        assertTrue(list.removeAll(removeListInOut));
        assertEquals(snapshotListSize - 5, list.size());
        for (String elementInOut : removeListInOut) {
            assertFalse(list.contains(elementInOut));
        }

        invalidateList.run();
        assertEquals(snapshotListSize, list.size());
        list.addAll(removeListIn);
        assertEquals(snapshotListSize + removeListIn.size(), list.size());
        assertTrue(list.removeAll(removeListIn));
        assertEquals(snapshotListSize - removeListIn.size(), list.size());
    }

    @Test
    void retainAll() {
        DIYarrayList<String> list = new DIYarrayList<>();
        assertThrows(UnsupportedOperationException.class, () -> list.retainAll(Collections.EMPTY_LIST));
    }

    @Test
    void clear() {
        DIYarrayList<String> list = new DIYarrayList<>();
        assertEquals(0, list.size());
        for (int i = 0; i < 1000; i++) {
            list.add(FILLER);
        }
        assertEquals(1000, list.size());
        list.clear();
        assertEquals(0, list.size());
    }

    @Test
    void get() {
        DIYarrayList<String> list = new DIYarrayList<>();
        for (int i = 0; i < 1000; i++) {
            list.add(FILLER + i);
        }
        assertThrows(IndexOutOfBoundsException.class, () -> list.get(-1));
        for (int i = 0; i < 1000; i++) {
            assertEquals(FILLER + i, list.get(i));
        }
        assertThrows(IndexOutOfBoundsException.class, () -> list.get(list.size()));
    }

    @Test
    void set() {
        DIYarrayList<String> list = new DIYarrayList<>();
        List<String> checkList = new ArrayList<>(1000);
        for (int i = 0; i < 1000; i++) {
            list.add(FILLER);
            checkList.add(FILLER + i);
        }
        for (int i = 0; i < 1000; i++) {
            list.set(i, FILLER + i);
        }

        assertArrayEquals(checkList.toArray(), list.toArray());
    }

    @Test
    void indexOf() {
    }

    @Test
    void lastIndexOf() {
    }

    @Test
    void subList() {
        DIYarrayList<Object> list = new DIYarrayList<>();
        assertThrows(UnsupportedOperationException.class, () -> list.subList(0, 1));
    }
}