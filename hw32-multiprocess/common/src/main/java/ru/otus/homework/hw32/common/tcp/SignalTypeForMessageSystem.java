package ru.otus.homework.hw32.common.tcp;

public enum SignalTypeForMessageSystem {
    ERROR, HANDLE, UNKNOWN, ADD_CLIENT, REMOVE_CLIENT, NEW_MESSAGE, CURRENT_QUEUE_SIZE;

    public static SignalTypeForMessageSystem valueOfOrUnknown(String value) {
        try {
            return valueOf(value);
        } catch (IllegalArgumentException e) {
            return SignalTypeForMessageSystem.UNKNOWN;
        }
    }
}
