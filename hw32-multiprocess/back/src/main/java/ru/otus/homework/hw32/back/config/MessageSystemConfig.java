package ru.otus.homework.hw32.back.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.otus.homework.hw32.back.data.core.service.DBServiceUser;
import ru.otus.homework.hw32.back.message.DBServiceUserHandler;
import ru.otus.messagesystem.HandlersStore;
import ru.otus.messagesystem.HandlersStoreImpl;
import ru.otus.messagesystem.MessageSystem;
import ru.otus.messagesystem.client.MsClient;
import ru.otus.messagesystem.client.MsClientImpl;
import ru.otus.messagesystem.message.MessageType;

@Configuration
public class MessageSystemConfig {

    @Value("${message-system.database-service-client-name}")
    private String databaseServiceClientName;

    @Bean
    public HandlersStore requestHandlerDatabaseStore(DBServiceUser dbService) {
        HandlersStore requestHandlerDatabaseStore = new HandlersStoreImpl();
        requestHandlerDatabaseStore.addHandler(MessageType.USER_DATA, new DBServiceUserHandler(dbService));
        return requestHandlerDatabaseStore;
    }

    @Bean
    public MsClient databaseMsClient(
            MessageSystem messageSystem,
            @Qualifier("requestHandlerDatabaseStore") HandlersStore handler) {
        MsClientImpl client = new MsClientImpl(databaseServiceClientName, messageSystem, handler, null);
        messageSystem.addClient(client);
        return client;
    }
}
