package ru.otus.homework.hw32;

import lombok.extern.slf4j.Slf4j;

import java.rmi.RemoteException;

@Slf4j
public class ServerImpl implements Server {

    @Override
    public void setCallback(ClientCallback callback) throws RemoteException {
        log.info("result {}", callback.workInServer("test"));
    }
}
