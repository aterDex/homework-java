package ru.otus.homework.hw32.common.tcp;

import java.util.UUID;

public interface SignalServerListener {

    void event(UUID connectIdentifier, Signal signal);

    void closeConnect(UUID connectWitchClose);
}
