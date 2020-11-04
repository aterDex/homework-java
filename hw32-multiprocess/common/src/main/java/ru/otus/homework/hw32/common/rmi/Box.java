package ru.otus.homework.hw32.common.rmi;

import ru.otus.messagesystem.client.ResultDataType;

public class Box extends ResultDataType {

    private String text;

    public Box() {
    }

    public Box(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return "Box{" +
                "text='" + text + '\'' +
                '}';
    }
}
