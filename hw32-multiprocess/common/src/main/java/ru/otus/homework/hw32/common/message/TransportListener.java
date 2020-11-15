package ru.otus.homework.hw32.common.message;

import ru.otus.messagesystem.message.Message;

public interface TransportListener {

    void reconnect();

    void disconnect();

    void handle(Message message);
}
