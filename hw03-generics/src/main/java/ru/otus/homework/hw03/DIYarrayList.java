package ru.otus.homework.hw03;

import java.util.*;

public class DIYarrayList<T> implements List<T> {

    private T[] storage;
    private int sizeList = 0;

    public DIYarrayList() {
        this(10);
    }

    public DIYarrayList(int startCapacity) {
        reInitCapacity(startCapacity);
    }

    @Override
    public int size() {
        return sizeList;
    }

    @Override
    public boolean isEmpty() {
        return sizeList == 0;
    }

    @Override
    public boolean contains(Object o) {
        Iterator<T> it = iterator();
        while (it.hasNext()) {
            if (Objects.equals(o, it.next())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Iterator<T> iterator() {
        return new DIYiterattor<>(storage, sizeList);
    }

    @Override
    public Object[] toArray() {
        return Arrays.copyOf(storage, sizeList);
    }

    @Override
    public <T1> T1[] toArray(T1[] a) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean add(T t) {
        checkAndResizeCapacityIfNeed(sizeList + 1);
        storage[sizeList++] = t;
        return true;
    }

    @Override
    public boolean remove(Object o) {
        Iterator<T> it = iterator();
        while (it.hasNext()) {
            if (Objects.equals(o, it.next())) {
                it.remove();
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean addAll(Collection<? extends T> c) {
        if (c == null) {
            throw new NullPointerException();
        }
        if (c.isEmpty()) {
            return false;
        }
        checkAndResizeCapacityIfNeed(sizeList + c.size());
        System.arraycopy(c.toArray(), 0, storage, sizeList, c.size());
        sizeList = sizeList + c.size();
        return true;
    }

    @Override
    public boolean addAll(int index, Collection<? extends T> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
        sizeList = 0;
        // Чтобы позволить GarbageCollector сделать свое черное дело
        Arrays.fill(storage, null);
    }

    @Override
    public T get(int index) {
        checkIndex(index);
        return storage[index];
    }

    @Override
    public T set(int index, T element) {
        checkIndex(index);
        T previousElement = storage[index];
        storage[index] = element;
        return previousElement;
    }

    @Override
    public void add(int index, T element) {
        checkIndex(index);
        checkAndResizeCapacityIfNeed(sizeList + 1);
        System.arraycopy(storage, index,
                storage, index + 1,
                sizeList - index);
        sizeList++;
        storage[index] = element;
    }

    @Override
    public T remove(int index) {
        checkIndex(index);
        T element = storage[index];
        System.arraycopy(storage, index,
                storage, index - 1,
                sizeList - index);
        sizeList--;
        // Мы через arrayCopy все сдвинули,
        // но один элемент крайний сейчас продублировался очищаем его
        storage[sizeList] = null;
        return element;
    }

    @Override
    public int indexOf(Object o) {
        for (int i = 0; i < sizeList; i++) {
            if (Objects.equals(o, storage[i])) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public int lastIndexOf(Object o) {
        for (int i = sizeList - 1; i >= 0; i--) {
            if (Objects.equals(o, storage[i])) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public ListIterator<T> listIterator() {
        return new DIYiterattor<>(storage, sizeList);
    }

    @Override
    public ListIterator<T> listIterator(int index) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<T> subList(int fromIndex, int toIndex) {
        throw new UnsupportedOperationException();
    }

    /**
     * Проверяем размер внутреннего хранилище, и пересоздам его
     *
     * @param newSize новый размер масива который будет установлен
     */
    protected void checkAndResizeCapacityIfNeed(int newSize) {
        if (storage.length < newSize) {
            reInitCapacity(newSize * 2);
        }
    }

    protected void reInitCapacity(int newCapacity) {
        if (storage == null) {
            storage = (T[]) new Object[newCapacity];
        } else {
            storage = Arrays.copyOf(storage, newCapacity);
        }
    }

    protected void checkIndex(int index) {
        if (index < 0 || index >= sizeList) {
            throw new IndexOutOfBoundsException();
        }
    }

    private class DIYiterattor<I> implements ListIterator<I> {

        private int currentPosition = -1;
        private final I[] storage;
        private final int sizeList;

        public DIYiterattor(I[] storage, int sizeList) {
            this.storage = storage;
            this.sizeList = sizeList;
        }

        @Override
        public boolean hasNext() {
            return (currentPosition + 1) < sizeList;
        }

        @Override
        public I next() {
            if (hasNext()) {
                return storage[++currentPosition];
            }
            throw new NoSuchElementException();
        }

        @Override
        public boolean hasPrevious() {
            return (currentPosition - 1) >= 0;
        }

        @Override
        public I previous() {
            if (hasPrevious()) {
                return storage[--currentPosition];
            }
            throw new NoSuchElementException();
        }

        @Override
        public int nextIndex() {
            if (hasNext()) {
                return currentPosition + 1;
            }
            return sizeList;
        }

        @Override
        public int previousIndex() {
            if (hasPrevious()) {
                return currentPosition - 1;
            }
            return -1;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void set(I i) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void add(I i) {
            throw new UnsupportedOperationException();
        }
    }
}
