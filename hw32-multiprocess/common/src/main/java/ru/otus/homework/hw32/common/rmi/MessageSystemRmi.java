package ru.otus.homework.hw32.common.rmi;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import ru.otus.messagesystem.MessageSystem;
import ru.otus.messagesystem.client.MsClient;
import ru.otus.messagesystem.message.Message;

import java.rmi.server.UnicastRemoteObject;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class MessageSystemRmi implements MessageSystem {

    private final MessageSystemRegisterByRmi register;
    private final Map<String, Binding> localBindings = new ConcurrentHashMap<>();

    public MessageSystemRmi(MessageSystemRegisterByRmi register) {
        this.register = register;
    }

    @Override
    @SneakyThrows
    public void addClient(MsClient msClient) {
        checkBinding(msClient);
        var binding = createAndRegisterBinding(msClient);
        try {
            register.addClient(msClient.getName(), binding.getStub());
        } catch (Exception e) {
            unregisterBinding(msClient.getName());
            throw e;
        }
    }

    @Override
    @SneakyThrows
    public void removeClient(String clientId) {
        if (!localBindings.containsKey(clientId)) {
            throw new RuntimeException(String.format("Не найден msClient с именем %s", clientId));
        }
        register.removeClient(clientId);
        unregisterBinding(clientId);
    }

    @Override
    @SneakyThrows
    public boolean newMessage(Message msg) {
        return register.newMessage(msg);
    }

    @Override
    public void dispose() throws InterruptedException {
        dispose(() -> {
        });
    }

    @Override
    public void dispose(Runnable callback) throws InterruptedException {
        for (String name : localBindings.keySet()) {
            try {
                removeClient(name);
            } catch (Exception e) {
                log.error("", e);
            }
        }
        callback.run();
    }

    @Override
    public void start() {
    }

    @Override
    @SneakyThrows
    public int currentQueueSize() {
        return register.currentQueueSize();
    }

    @SneakyThrows
    private void unregisterBinding(String name) {
        Binding binding = localBindings.get(name);
        if (binding != null) {
            localBindings.remove(name);
            UnicastRemoteObject.unexportObject(binding.getHandler(), false);
        }
    }

    @SneakyThrows
    private Binding createAndRegisterBinding(MsClient msClient) {
        var handler = new HandlerMessageByRmiServer(msClient);
        var handlerStub = (HandleMessageByRmi) UnicastRemoteObject.exportObject(handler, 0);
        Binding binding = new Binding(handler, handlerStub, msClient);
        localBindings.put(msClient.getName(), binding);
        return binding;
    }

    private void checkBinding(MsClient msClient) {
        if (localBindings.containsKey(msClient.getName())) {
            throw new RuntimeException(String.format("Уже зарегистрирован msClient с именем %s", msClient.getName()));
        }
    }

    @Getter
    @AllArgsConstructor
    private class Binding {
        private final HandlerMessageByRmiServer handler;
        private final HandleMessageByRmi stub;
        private final MsClient msClient;
    }
}
