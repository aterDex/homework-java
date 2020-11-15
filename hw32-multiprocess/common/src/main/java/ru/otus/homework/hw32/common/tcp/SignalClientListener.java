package ru.otus.homework.hw32.common.tcp;

public interface SignalClientListener {

    /**
     * Attention! It should be fast!
     *
     * @param signal
     * @param client
     */
    void event(Signal signal, SignalTcpClient client);

    void closeConnect(SignalTcpClient client);
}
