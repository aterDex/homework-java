package ru.otus.homework.hw32.common;

import lombok.SneakyThrows;
import ru.otus.homework.hw32.common.message.MessageSystemRemote;
import ru.otus.homework.hw32.common.rmi.MessageSystemRegisterByRmi;
import ru.otus.homework.hw32.common.rmi.MessageSystemRegisterByRmiAdapter;
import ru.otus.homework.hw32.common.rmi.TransportByRmi;
import ru.otus.messagesystem.MessageSystem;

import java.rmi.server.UnicastRemoteObject;

public class MessageSystemProviderRmi implements MessageSystemProvider {

    private static final String description = "RMI";

    @Override
    public DisposableMessageSystem init(MessageSystem core) throws Exception {
        var messageSystemRegisterRmi = new MessageSystemRegisterByRmiAdapter(core, null);
        var messageSystemRegisterRmiStub = (MessageSystemRegisterByRmi) UnicastRemoteObject.exportObject(messageSystemRegisterRmi, 0);
        var transportByRmi = new TransportByRmi(messageSystemRegisterRmiStub);
        var messageSystemRmi = new MessageSystemRemote(transportByRmi, null);
        messageSystemRmi.start();
        return new DisposableMessageSystem(description, messageSystemRmi, new Runnable() {
            @Override
            @SneakyThrows
            public void run() {
                messageSystemRmi.dispose();
                transportByRmi.dispose();
                UnicastRemoteObject.unexportObject(messageSystemRegisterRmi, true);
            }
        });
    }

    @Override
    public String getDescription() {
        return description;
    }
}
