package ru.otus.homework.hw32.common.rmi;

import java.io.Serializable;

public class RmiConnectInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
