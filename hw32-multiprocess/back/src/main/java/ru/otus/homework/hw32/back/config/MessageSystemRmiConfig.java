package ru.otus.homework.hw32.back.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.otus.homework.hw32.back.data.core.service.DBServiceUser;
import ru.otus.homework.hw32.back.message.DBServiceUserHandler;
import ru.otus.homework.hw32.common.rmi.MessageSystemRegisterByRmi;
import ru.otus.homework.hw32.common.rmi.MsClientByRmiClient;
import ru.otus.messagesystem.HandlersStore;
import ru.otus.messagesystem.HandlersStoreImpl;
import ru.otus.messagesystem.client.CallbackRegistry;
import ru.otus.messagesystem.client.CallbackRegistryImpl;
import ru.otus.messagesystem.message.MessageType;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

@Configuration
public class MessageSystemRmiConfig {

    @Value("${rmi.registry-port}")
    private int registryPort;

    @Value("${rmi.message-system.name}")
    private String messageSystemName;

    @Value("${message-system.database-service-client-name}")
    private String databaseServiceClientName;

    @Bean
    public Registry registry() throws RemoteException {
        return LocateRegistry.getRegistry(registryPort);
    }

    @Bean
    public MessageSystemRegisterByRmi messageSystemRegister(Registry registry) throws Exception {
        return (MessageSystemRegisterByRmi) registry.lookup(messageSystemName);
    }

    @Bean
    public HandlersStore requestHandlerDatabaseStore(DBServiceUser dbService) {
        HandlersStore requestHandlerDatabaseStore = new HandlersStoreImpl();
        requestHandlerDatabaseStore.addHandler(MessageType.USER_DATA, new DBServiceUserHandler(dbService));
        return requestHandlerDatabaseStore;
    }

    @Bean(initMethod = "register", destroyMethod = "unregister")
    public MsClientByRmiClient databaseMsClient(
            MessageSystemRegisterByRmi messageSystemRegister,
            @Qualifier("requestHandlerDatabaseStore") HandlersStore handler) {
        return new MsClientByRmiClient(databaseServiceClientName, messageSystemRegister, handler, null);
    }
}
