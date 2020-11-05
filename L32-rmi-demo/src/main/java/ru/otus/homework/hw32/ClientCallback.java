package ru.otus.homework.hw32;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ClientCallback extends Remote {

    String workInServer(String text) throws RemoteException;
}
