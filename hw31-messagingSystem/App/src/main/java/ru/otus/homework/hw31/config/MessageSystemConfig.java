package ru.otus.homework.hw31.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.otus.homework.hw31.data.core.service.DBServiceUser;
import ru.otus.homework.hw31.message.CreateUserRequestHandler;
import ru.otus.homework.hw31.message.CreateUserResponseHandler;
import ru.otus.homework.hw31.message.FrontendService;
import ru.otus.homework.hw31.message.FrontendServiceImpl;
import ru.otus.messagesystem.HandlersStore;
import ru.otus.messagesystem.HandlersStoreImpl;
import ru.otus.messagesystem.MessageSystem;
import ru.otus.messagesystem.MessageSystemImpl;
import ru.otus.messagesystem.client.CallbackRegistry;
import ru.otus.messagesystem.client.CallbackRegistryImpl;
import ru.otus.messagesystem.client.MsClient;
import ru.otus.messagesystem.client.MsClientImpl;
import ru.otus.messagesystem.message.MessageType;

@Slf4j
@Configuration
public class MessageSystemConfig {

    private static final String FRONTEND_SERVICE_CLIENT_NAME = "frontendService";
    private static final String DATABASE_SERVICE_CLIENT_NAME = "databaseService";

    @Bean
    public MessageSystem messageSystem() {
        return new MessageSystemImpl();
    }

    @Bean
    public CallbackRegistry callbackRegistry() {
        return new CallbackRegistryImpl();
    }

    @Bean
    public MsClient databaseMsClient(MessageSystem messageSystem, CallbackRegistry callbackRegistry, DBServiceUser dbService) {
        HandlersStore requestHandlerDatabaseStore = new HandlersStoreImpl();
        requestHandlerDatabaseStore.addHandler(MessageType.USER_DATA, new CreateUserRequestHandler(dbService));
        MsClientImpl client = new MsClientImpl(DATABASE_SERVICE_CLIENT_NAME,
                messageSystem, requestHandlerDatabaseStore, callbackRegistry);
        messageSystem.addClient(client);
        return client;
    }

    @Bean
    public MsClient frontendMsClient(MessageSystem messageSystem, CallbackRegistry callbackRegistry) {
        HandlersStore requestHandlerFrontendStore = new HandlersStoreImpl();
        requestHandlerFrontendStore.addHandler(MessageType.USER_DATA, new CreateUserResponseHandler(callbackRegistry));
        MsClientImpl client = new MsClientImpl(FRONTEND_SERVICE_CLIENT_NAME,
                messageSystem, requestHandlerFrontendStore, callbackRegistry);
        messageSystem.addClient(client);
        return client;
    }

    @Bean
    public FrontendService frontendService(@Qualifier("frontendMsClient") MsClient client) {
        return new FrontendServiceImpl(client, DATABASE_SERVICE_CLIENT_NAME);
    }
}
