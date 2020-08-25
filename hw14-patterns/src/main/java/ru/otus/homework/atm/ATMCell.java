package ru.otus.homework.atm;

public interface ATMCell extends Memento {

    /**
     * @return тип купюр, которые содержит эта ячека
     */
    Denomination getDenomination();

    /**
     * @return Количество купюр в ячеки
     */
    int getCount();

    /**
     * Помещаем купюры в ячеку
     *
     * @param count количество купюр
     */
    void put(int count);

    /**
     * Забираем купюры на выдочу
     *
     * @param count количество купюр
     */
    void cashOut(int count);

    /**
     * @return Максимальный количество купюр в ячейки
     */
    int getLimit();

    /**
     * @return Количество свободных мест в ячейки
     */
    int getFree();

    /**
     * @return Сумма денежных средств хранимая в ячейки
     */
    long getAmount();
}
