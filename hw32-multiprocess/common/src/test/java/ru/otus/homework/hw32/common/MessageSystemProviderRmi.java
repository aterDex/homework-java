package ru.otus.homework.hw32.common;

import lombok.SneakyThrows;
import ru.otus.homework.hw32.common.rmi.MessageSystemRegisterByRmi;
import ru.otus.homework.hw32.common.rmi.MessageSystemRegisterByRmiAdapter;
import ru.otus.homework.hw32.common.rmi.MessageSystemRmi;
import ru.otus.messagesystem.MessageSystem;

import java.rmi.server.UnicastRemoteObject;

public class MessageSystemProviderRmi implements MessageSystemProvider {

    private static final String description = "RMI";

    @Override
    public DisposableMessageSystem init(MessageSystem core) throws Exception {
        var messageSystemRegisterRmi = new MessageSystemRegisterByRmiAdapter(core, null);
        var messageSystemRegisterRmiStub = (MessageSystemRegisterByRmi) UnicastRemoteObject.exportObject(messageSystemRegisterRmi, 0);

        var messageSystemRmi = new MessageSystemRmi(messageSystemRegisterRmiStub);
        return new DisposableMessageSystem(description, messageSystemRmi, new Runnable() {
            @Override
            @SneakyThrows
            public void run() {
                messageSystemRmi.dispose();
                UnicastRemoteObject.unexportObject(messageSystemRegisterRmi, true);
            }
        });
    }

    @Override
    public String getDescription() {
        return description;
    }
}
