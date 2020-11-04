package ru.otus.homework.hw32.common.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface TestCH extends Remote {

    String aa(String bb) throws RemoteException;
}
