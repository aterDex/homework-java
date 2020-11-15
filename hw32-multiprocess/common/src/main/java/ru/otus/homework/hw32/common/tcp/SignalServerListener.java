package ru.otus.homework.hw32.common.tcp;

import java.util.UUID;

public interface SignalServerListener {

    /**
     * Attention! It should be fast!
     *
     * @param connectIdentifier
     * @param signal
     * @param server
     */
    void event(UUID connectIdentifier, Signal signal, SignalTcpServer server);

    void disconnect(UUID connectWitchClose, SignalTcpServer server);
}
