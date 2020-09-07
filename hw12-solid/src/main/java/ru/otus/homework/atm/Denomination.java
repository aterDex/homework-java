package ru.otus.homework.atm;

public enum Denomination {

    D50(50), D100(100), D200(200), D500(500), D1000(1000), D2000(2000), D5000(5000);

    private final int cost;

    Denomination(int cost) {
        this.cost = cost;
    }

    public int getCost() {
        return cost;
    }
}
