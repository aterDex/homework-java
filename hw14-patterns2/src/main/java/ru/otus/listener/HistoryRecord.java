package ru.otus.listener;

import ru.otus.Message;

import java.time.ZonedDateTime;

public class HistoryRecord {

    private final Message oldMsg;
    private final Message newMsg;
    private final ZonedDateTime timeChange;

    public HistoryRecord(Message oldMsg, Message newMsg) {
        this.oldMsg = oldMsg;
        this.newMsg = newMsg;
        this.timeChange = ZonedDateTime.now();
    }

    public Message getOldMsg() {
        return oldMsg;
    }

    public Message getNewMsg() {
        return newMsg;
    }

    public ZonedDateTime getTimeChange() {
        return timeChange;
    }

    @Override
    public String toString() {
        return "HistoryRecord{" +
                "oldMsg=" + oldMsg +
                ", newMsg=" + newMsg +
                ", timeChange=" + timeChange +
                '}';
    }
}
