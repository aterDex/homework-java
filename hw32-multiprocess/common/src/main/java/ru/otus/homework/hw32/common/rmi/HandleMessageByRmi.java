package ru.otus.homework.hw32.common.rmi;

import ru.otus.messagesystem.message.Message;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface HandleMessageByRmi extends Remote {

    void handle(Message msg) throws RemoteException;
}
