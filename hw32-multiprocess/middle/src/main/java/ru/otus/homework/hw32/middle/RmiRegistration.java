package ru.otus.homework.hw32.middle;

import lombok.Getter;
import lombok.SneakyThrows;

import java.rmi.Remote;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

@Getter
public class RmiRegistration<T extends Remote> {

    private final Registry registry;
    private final T object;
    private final int port;
    private final String name;
    private Remote stub;

    public RmiRegistration(Registry registry, T object, String name) {
        this(registry, object, 0, name);
    }

    public RmiRegistration(Registry registry, T object, int port, String name) {
        this.registry = registry;
        this.object = object;
        this.port = port;
        this.name = name;
    }

    @SneakyThrows
    public void register() {
        stub = UnicastRemoteObject.exportObject(object, port);
        registry.rebind(name, stub);
    }

    @SneakyThrows
    public void unregister() {
        registry.unbind(name);
        UnicastRemoteObject.unexportObject(object, false);
        stub = null;
    }
}
