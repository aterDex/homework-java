package ru.otus.homework.hw03;

import java.util.*;
import java.util.function.Predicate;

/**
 * Учебная реализация коллекции.
 * На основе хранения в единном массиве.
 * Не поддерживаются методы:
 * <ul>
 * <li>{@link DIYarrayList#toArray(Object[])}</li>
 * <li>{@link DIYarrayList#retainAll(Collection)}</li>
 * <li>{@link DIYarrayList#subList(int, int)}</li>
 * </ul>
 */
public final class DIYarrayList<T> implements List<T> {

    /**
     * Начальный размер массива по умолчанию
     */
    private static final int DEFAULT_CAPACITY = 10;

    /**
     * Предельный размер массива, который можем создать для хранения
     * (Нельзя выставлять отрицательно значение)
     */
    private static final int LIMIT_SIZE = Integer.MAX_VALUE;

    /**
     * Множетель отвечает за то на сколько вырастит массив,
     * при его переинициализации (как минимум всегда на 1 элемент)
     * (Нельзя выставлять отрицательно значение, или не число)
     */
    private static final double FACTOR_GROW = 1.5;

    /**
     * Храним элементы листа в этом массиве
     */
    private T[] storage;

    /**
     * Текущий размер листа
     */
    private int sizeList = 0;

    /**
     * счетчик модификаций листа (любые действия что приводят к изменению sizeList)
     */
    private int modificationCounter = 0;

    public DIYarrayList() {
        this(DEFAULT_CAPACITY);
    }

    /**
     * @param startCapacity начальная емкость массива, который находится в основе этого контейнера (больше или равен 0)
     */
    public DIYarrayList(int startCapacity) {
        if (startCapacity == 0) {
            initCapacity(DEFAULT_CAPACITY);
        } else if (startCapacity < 0 || startCapacity > LIMIT_SIZE) {
            throw new IllegalArgumentException("Неверный размер массива: " + startCapacity + ". Размер массива может быть от 1 до " + LIMIT_SIZE);
        }
        initCapacity(startCapacity);
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
        return new DIYiterattor<>(modificationCounter);
    }

    @Override
    public Object[] toArray() {
        return Arrays.copyOf(storage, sizeList);
    }

    /**
     * Unsupported Operation
     */
    @Override
    public <T1> T1[] toArray(T1[] a) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean add(T t) {
        checkAndGrowCapacityIfNeed(1);
        storage[sizeList++] = t;
        modificationCounter++;
        return true;
    }

    @Override
    public T remove(int index) {
        checkIndex(index);
        T element = storage[index];
        System.arraycopy(storage, index + 1,
                storage, index,
                sizeList - index);
        sizeList--;
        // Мы через arrayCopy все сдвинули,
        // но один элемент крайний сейчас продублировался очищаем его
        storage[sizeList] = null;
        modificationCounter++;
        return element;
    }

    @Override
    public boolean remove(Object o) {
        int index = indexOf(o);
        if (index >= 0) {
            remove(index);
            return true;
        }
        return false;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return c.stream().filter(this::removeAllEntry).count() > 0;
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return c.parallelStream().filter(Predicate.not(this::contains)).findAny().isEmpty();
    }

    @Override
    public boolean addAll(Collection<? extends T> c) {
        // NullPointerException Ok
        if (c.isEmpty()) {
            return false;
        }
        checkAndGrowCapacityIfNeed(c.size());
        System.arraycopy(c.toArray(), 0, storage, sizeList, c.size());
        sizeList += c.size();
        modificationCounter++;
        return true;
    }

    @Override
    public boolean addAll(int index, Collection<? extends T> c) {
        checkIndex(index);
        // NullPointerException Ok
        if (c.isEmpty()) {
            return false;
        }
        checkAndGrowCapacityIfNeed(c.size());
        System.arraycopy(storage, index, storage, index + c.size(), c.size());
        System.arraycopy(c.toArray(), 0, storage, index, c.size());
        sizeList += c.size();
        modificationCounter++;
        return true;
    }

    /**
     * Unsupported Operation
     */
    @Override
    public boolean retainAll(Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
        sizeList = 0;
        modificationCounter++;
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
        checkAndGrowCapacityIfNeed(1);
        System.arraycopy(storage, index,
                storage, index + 1,
                sizeList - index);
        sizeList++;
        storage[index] = element;
        modificationCounter++;
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
        return new DIYiterattor<>(modificationCounter);
    }

    @Override
    public ListIterator<T> listIterator(int index) {
        checkIndex(index);
        return new DIYiterattor<>(modificationCounter, index);
    }

    /**
     * Unsupported Operation
     */
    @Override
    public List<T> subList(int fromIndex, int toIndex) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Spliterator<T> spliterator() {
        return Arrays.spliterator(storage, 0, sizeList);
    }

    /**
     * Удаляем все вхождения элемента
     *
     * @param o объект который будет полностью удален из коллекции
     * @return true если коллекция была изменена
     */
    private boolean removeAllEntry(Object o) {
        boolean wasRemove = false;
        Iterator<T> it = iterator();
        while (it.hasNext()) {
            if (Objects.equals(o, it.next())) {
                it.remove();
                wasRemove = true;
            }
        }
        return wasRemove;
    }

    /**
     * Проверяем размер внутреннего хранилище, и пересоздам его
     *
     * @param addQuantity количество элементов которые надо добавить
     */
    private void checkAndGrowCapacityIfNeed(int addQuantity) {
        try {
            // Не допускаем попытки записи в массив элементов больше чем Integer.MAX_VALUE
            // Через контроль переполнения
            int newSize = Math.addExact(sizeList, addQuantity);
            if (newSize > LIMIT_SIZE) {
                throw new IllegalArgumentException("Элементы не могут быть добавлены. Размер коллекции не может превышать " + LIMIT_SIZE + " элементов.");
            }
            if (storage.length < newSize) {
                // + 1 нужен, для защиты если FACTOR_GROW будет слишком маленький
                Double newSizeAndFactorGrow = newSize * FACTOR_GROW + 1;
                // Если случилос вдруг переполнения (выбран большой FACTOR_GROW например)
                // То задаем максимально возможный размер массива LIMIT_SIZE
                if (newSizeAndFactorGrow.isInfinite() ||
                        newSizeAndFactorGrow.longValue() < 0 ||
                        newSizeAndFactorGrow.longValue() >= LIMIT_SIZE) {
                    reInitCapacity(LIMIT_SIZE);
                } else {
                    reInitCapacity(newSizeAndFactorGrow.intValue());
                }
            }
        } catch (ArithmeticException e) {
            throw new IllegalArgumentException("Элементы не могут быть добавлены. Размер коллекции не может превышать " + LIMIT_SIZE + " элементов.");
        }
    }

    @SuppressWarnings("unchecked")
    private void initCapacity(int newCapacity) {
        storage = (T[]) new Object[newCapacity];
    }

    /**
     * Переинциализируем массив где все храним, естественно с копирование предыдущих данных
     *
     * @param newCapacity новый размер массива
     */
    private void reInitCapacity(int newCapacity) {
        storage = Arrays.copyOf(storage, newCapacity);
    }

    private void checkIndex(int index) {
        if (index < 0 || index >= sizeList) {
            throw new IndexOutOfBoundsException();
        }
    }

    private class DIYiterattor<I> implements ListIterator<I> {

        private int modification;
        private boolean wasAdd = false;
        private boolean wasRemoved = false;
        private int currentPosition;

        /**
         * @param modification фиксируем текущею модификацию листа
         * @param startIndex   Стартовая позиция для итератора (то что вернет первый next)
         */
        public DIYiterattor(int modification, int startIndex) {
            currentPosition = startIndex - 1;
            this.modification = modification;
        }

        /**
         * @param modification фиксируем текущею модификацию листа
         */
        public DIYiterattor(int modification) {
            this(modification, 0);
        }

        @Override
        public boolean hasNext() {
            return (currentPosition + 1) < DIYarrayList.this.sizeList;
        }

        @Override
        @SuppressWarnings("unchecked")
        public I next() {
            checkListModification();
            if (hasNext()) {
                invalidateState();
                return (I) DIYarrayList.this.storage[++currentPosition];
            }
            throw new NoSuchElementException();
        }

        @Override
        public boolean hasPrevious() {
            return currentPosition >= 0;
        }

        @Override
        @SuppressWarnings("unchecked")
        public I previous() {
            checkListModification();
            if (hasPrevious()) {
                invalidateState();
                return (I) DIYarrayList.this.storage[currentPosition--];
            }
            throw new NoSuchElementException();
        }

        @Override
        public int nextIndex() {
            return hasNext() ? currentPosition + 1 : DIYarrayList.this.sizeList;
        }

        @Override
        public int previousIndex() {
            return hasPrevious() ? currentPosition : -1;
        }

        @Override
        public void remove() {
            checkListModification();
            if (wasAdd || wasRemoved || isNotLegalCurrentPosition()) {
                throw new IllegalStateException();
            }
            wasRemoved = true;
            DIYarrayList.this.remove(currentPosition--);
            updateListModification();
        }

        @Override
        @SuppressWarnings("unchecked")
        public void set(I i) {
            checkListModification();
            if (wasAdd || wasRemoved || isNotLegalCurrentPosition()) {
                throw new IllegalStateException();
            }
            DIYarrayList.this.set(currentPosition, (T) i);
        }

        @Override
        @SuppressWarnings("unchecked")
        public void add(I i) {
            checkListModification();
            wasAdd = true;
            DIYarrayList.this.add(++currentPosition, (T) i);
            updateListModification();
        }

        /**
         * Сбрасываем состояние итератора для текущей позиции.
         */
        private void invalidateState() {
            wasAdd = false;
            wasRemoved = false;
        }

        private boolean isNotLegalCurrentPosition() {
            return currentPosition < 0 || currentPosition >= DIYarrayList.this.sizeList;
        }

        private void checkListModification() {
            if (modification != DIYarrayList.this.modificationCounter) {
                throw new ConcurrentModificationException();
            }
        }

        private void updateListModification() {
            modification = DIYarrayList.this.modificationCounter;
        }
    }
}
