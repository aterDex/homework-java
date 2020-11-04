package ru.otus.homework.hw32.common.rmi;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class TestCHImpl extends UnicastRemoteObject implements TestCH {

    private static final long serialVersionUID = 1L;

    public TestCHImpl(int port) throws RemoteException {
        super(port);
    }

    @Override
    public String aa(String bb) {
        return bb + "AA";
    }
}
