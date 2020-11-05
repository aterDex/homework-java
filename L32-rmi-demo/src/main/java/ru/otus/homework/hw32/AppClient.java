package ru.otus.homework.hw32;

import lombok.extern.slf4j.Slf4j;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

@Slf4j
public class AppClient {

    private static final int REGISTRY_PORT = 1099;

    public static void main(String[] args) throws Exception {
        log.info("client pid: {}", ProcessHandle.current().pid());

        Registry registry = LocateRegistry.getRegistry(REGISTRY_PORT);
        Server server = (Server) registry.lookup("server");

        ClientCallback client = new ClientCallbackImpl();
        ClientCallback stub =
                (ClientCallback) UnicastRemoteObject.exportObject(client, 0);
        log.info("++++");
        server.setCallback(stub);
        Thread.sleep(5000);
        UnicastRemoteObject.unexportObject(client, false);
    }
}
