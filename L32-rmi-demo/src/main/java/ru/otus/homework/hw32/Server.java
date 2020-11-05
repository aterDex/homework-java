package ru.otus.homework.hw32;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Server extends Remote {

    void setCallback(ClientCallback callback) throws RemoteException;
}
