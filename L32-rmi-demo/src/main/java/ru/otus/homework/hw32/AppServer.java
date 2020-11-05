package ru.otus.homework.hw32;

import lombok.extern.slf4j.Slf4j;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

@Slf4j
public class AppServer {

    public static final int REGISTRY_PORT = 1099;

    public static void main(String[] args) throws Exception {
        log.info("server pid: {}", ProcessHandle.current().pid());

        ServerImpl server = new ServerImpl();
        Server stub =
                (Server) UnicastRemoteObject.exportObject(server, 0);
        Registry registry = LocateRegistry.createRegistry(REGISTRY_PORT);
        registry.rebind("server", stub);
        log.info("start");
    }
}
