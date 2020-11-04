package ru.otus.homework.hw32.common.rmi;

import ru.otus.messagesystem.HandlersStore;
import ru.otus.messagesystem.client.MsClient;
import ru.otus.messagesystem.message.Message;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class HandleMessageByRmiServer extends UnicastRemoteObject implements HandleMessageByRmi {

    private final MsClient client;

    protected HandleMessageByRmiServer(int port, MsClient client) throws RemoteException {
        super(port);
        this.client = client;
    }

    @Override
    public void handle(Message msg) throws RemoteException {
        client.handle(msg);
    }
}
