package ru.otus.homework.hw32.common.rmi;

import lombok.extern.slf4j.Slf4j;
import ru.otus.messagesystem.client.MsClient;
import ru.otus.messagesystem.message.Message;

import java.rmi.RemoteException;

@Slf4j
public class HandlerMessageByRmiServer implements HandleMessageByRmi {

    private final MsClient client;

    protected HandlerMessageByRmiServer(MsClient client) throws RemoteException {
        this.client = client;
    }

    @Override
    public void handle(Message msg) throws RemoteException {
        log.info("invoice rmi handler");
        client.handle(msg);
    }
}
