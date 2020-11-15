package ru.otus.homework.hw32.common.message;

import ru.otus.messagesystem.message.Message;

public interface TransportForMessageSystem {
    boolean sendNewMessage(Message msg);

    void addListener(TransportListener o);

    void removeListener(TransportListener o);

    int sendCurrentQueueSize();

    void sendRemoveClient(String clientId);

    void sendAddClient(String name);

    boolean isConnected();
}
