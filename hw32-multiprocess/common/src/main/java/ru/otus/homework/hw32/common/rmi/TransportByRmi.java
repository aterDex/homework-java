package ru.otus.homework.hw32.common.rmi;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import ru.otus.homework.hw32.common.message.TransportForMessageSystem;
import ru.otus.homework.hw32.common.message.TransportListener;
import ru.otus.messagesystem.message.Message;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Slf4j
public class TransportByRmi implements TransportForMessageSystem {

    private final MessageSystemRegisterByRmi register;
    private final HandlerMessageByRmi handler;
    private final HandlerMessageByRmi handlerStub;
    private final List<TransportListener> listeners = new CopyOnWriteArrayList<>();

    public TransportByRmi(MessageSystemRegisterByRmi register) throws RemoteException {
        this.register = register;
        handler = new HandlerMessageByRmiImpl();
        handlerStub = (HandlerMessageByRmi) UnicastRemoteObject.exportObject(handler, 0);
    }

    @Override
    @SneakyThrows
    public boolean sendNewMessage(Message msg) {
        return register.newMessage(msg);
    }

    @Override
    public void addListener(TransportListener o) {
        listeners.add(o);
    }

    @Override
    public void removeListener(TransportListener o) {
        listeners.remove(o);
    }

    @Override
    @SneakyThrows
    public int sendCurrentQueueSize() {
        return register.currentQueueSize();
    }

    @Override
    @SneakyThrows
    public void sendRemoveClient(String clientId) {
        register.removeClient(clientId);
    }

    @Override
    @SneakyThrows
    public void sendAddClient(String name) {
        register.addClient(name, handlerStub);
    }

    @Override
    public boolean isConnected() {
        return true;
    }

    public void dispose() throws RemoteException {
        UnicastRemoteObject.unexportObject(handler, true);
    }

    private class HandlerMessageByRmiImpl implements HandlerMessageByRmi {
        @Override
        public void handle(Message msg) throws RemoteException {
            for (TransportListener listener : listeners) {
                try {
                    listener.handle(msg);
                } catch (Exception e) {
                    log.error("", e);
                }
            }
        }
    }
}
