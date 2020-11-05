package ru.otus.homework.hw32.middle;

import ru.otus.homework.hw32.common.rmi.MessageSystemRegisterByRmi;
import ru.otus.homework.hw32.common.rmi.MessageSystemRegisterByRmiServer;
import ru.otus.messagesystem.MessageSystemImpl;

import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;

//@SpringBootApplication
public class App {
    private static final int REGISTRY_PORT = 13030;
    private static final int SERVER_PORT = 13031;

    public static void main(String[] args) throws Exception {
//        TestCH tt = new TestCHImpl(SERVER_PORT);
//        LocateRegistry.createRegistry(REGISTRY_PORT);
//        Naming.rebind("//localhost/TechCH", tt);
//        SpringApplication.run(App.class, args);

        MessageSystemImpl messageSystem = new MessageSystemImpl(true);
        MessageSystemRegisterByRmi messageSystemRmi = new MessageSystemRegisterByRmiServer(SERVER_PORT, REGISTRY_PORT, messageSystem);
        LocateRegistry.createRegistry(REGISTRY_PORT);
        Naming.rebind("//localhost:13030/MessageSystemRegister", messageSystemRmi);
    }
}
