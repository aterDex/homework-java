package ru.otus.homework.hw32.common.tcp;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import ru.otus.homework.hw32.common.message.TransportForMessageSystem;
import ru.otus.homework.hw32.common.message.TransportListener;
import ru.otus.messagesystem.message.Message;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

import static ru.otus.homework.hw32.common.tcp.SignalForMessageSystemHelper.*;
import static ru.otus.homework.hw32.common.tcp.SignalTypeForMessageSystem.*;

@Slf4j
public class TransportBySignalTcp implements TransportForMessageSystem {

    private final SignalTcpClient client;
    private final List<TransportListener> listeners = new CopyOnWriteArrayList<>();

    public TransportBySignalTcp(SignalTcpClient client) {
        this.client = client;
        client.addListener(new SignalClientListenerImpl());
    }

    @Override
    @SneakyThrows
    public boolean sendNewMessage(Message msg) {
        return processAnswerClient(
                new Signal(NEW_MESSAGE.name(), UUID.randomUUID(), msg),
                client,
                x -> (Boolean) x.getBody());
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
        return processAnswerClient(
                new Signal(CURRENT_QUEUE_SIZE.name(), UUID.randomUUID(), null),
                client,
                x -> (Integer) x.getBody());
    }

    @Override
    @SneakyThrows
    public void sendRemoveClient(String clientId) {
        processAnswerClient(new Signal(REMOVE_CLIENT.name(), UUID.randomUUID(), clientId), client, null);
    }

    @Override
    @SneakyThrows
    public void sendAddClient(String name) {
        processAnswerClient(new Signal(ADD_CLIENT.name(), UUID.randomUUID(), name), client, null);
    }

    @Override
    public boolean isConnected() {
        return client.isConnected();
    }

    private void handleSignal(Signal signal, SignalTcpClient client) {
        try {
            switch (SignalTypeForMessageSystem.valueOfOrUnknown(signal.getTag())) {
                case HANDLE:
                    try {
                        for (TransportListener listener : listeners) {
                            listener.handle((Message) signal.getBody());
                        }
                        client.send(answerOk(signal));
                    } catch (Exception e) {
                        log.error("", e);
                        client.send(answerError(signal, e.getMessage()));
                    }
                    break;
                case ERROR:
                    log.error("Error signal get: " + signal);
                    break;
                default:
                    client.send(answerError(signal, String.format("Unknown signal %s", signal.getTag())));
                    break;
            }
        } catch (Exception e) {
            log.error("Error signal: " + signal, e);
        }
    }

    private class SignalClientListenerImpl implements SignalClientListener {
        @Override
        public void event(Signal signal, SignalTcpClient client) {
            handleSignal(signal, client);
        }

        @Override
        public void closeConnect(SignalTcpClient client) {
            for (TransportListener listener : listeners) {
                listener.disconnect();
            }
        }
    }
}
