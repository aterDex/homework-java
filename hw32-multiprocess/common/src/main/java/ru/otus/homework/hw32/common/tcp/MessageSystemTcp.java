package ru.otus.homework.hw32.common.tcp;

import ru.otus.messagesystem.MessageSystem;
import ru.otus.messagesystem.client.MsClient;
import ru.otus.messagesystem.message.Message;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MessageSystemTcp implements MessageSystem {

    private final String ip;
    private final int port;

    public MessageSystemTcp(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    @Override
    public void addClient(MsClient msClient) {
    }

    @Override
    public void removeClient(String clientId) {
    }

    @Override
    public boolean newMessage(Message msg) {
        return false;
    }

    @Override
    public void dispose() throws InterruptedException {
    }

    @Override
    public void dispose(Runnable callback) throws InterruptedException {
    }

    @Override
    public void start() {

    }

    @Override
    public int currentQueueSize() {
        return 0;
    }
}
