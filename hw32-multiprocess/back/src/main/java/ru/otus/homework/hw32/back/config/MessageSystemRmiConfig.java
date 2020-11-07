package ru.otus.homework.hw32.back.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import ru.otus.homework.hw32.common.rmi.MessageSystemRegisterByRmi;
import ru.otus.homework.hw32.common.rmi.MessageSystemRmi;
import ru.otus.messagesystem.MessageSystem;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

@Configuration
@Profile("RMI")
public class MessageSystemRmiConfig {

    @Value("${rmi.registry-port}")
    private int registryPort;

    @Value("${rmi.message-system.name}")
    private String messageSystemName;

    @Bean
    public Registry registry() throws RemoteException {
        return LocateRegistry.getRegistry(registryPort);
    }

    @Bean
    public MessageSystemRegisterByRmi messageSystemRegister(Registry registry) throws Exception {
        return (MessageSystemRegisterByRmi) registry.lookup(messageSystemName);
    }

    @Bean
    public MessageSystem messageSystem(MessageSystemRegisterByRmi messageSystemRegister) {
        return new MessageSystemRmi(messageSystemRegister);
    }
}
