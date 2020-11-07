package ru.otus.homework.hw32.common.rmi;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import ru.otus.homework.hw32.common.message.CallbackRequestHandler;
import ru.otus.messagesystem.HandlersStore;
import ru.otus.messagesystem.HandlersStoreImpl;
import ru.otus.messagesystem.MessageSystemImpl;
import ru.otus.messagesystem.RequestHandler;
import ru.otus.messagesystem.client.CallbackRegistryImpl;
import ru.otus.messagesystem.client.MessageCallback;
import ru.otus.messagesystem.client.MsClientImpl;
import ru.otus.messagesystem.client.ResultDataType;
import ru.otus.messagesystem.message.Message;
import ru.otus.messagesystem.message.MessageBuilder;
import ru.otus.messagesystem.message.MessageHelper;
import ru.otus.messagesystem.message.MessageType;

import java.rmi.server.UnicastRemoteObject;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
public class RmiTest {

    private static final String FRONTEND_SERVICE_CLIENT_NAME = "frontendService";
    private static final String DATABASE_SERVICE_CLIENT_NAME = "databaseService";

    @Test
    void CheckRMI() throws Exception {
        var messageSystem = new MessageSystemImpl(true);
        var callbackRegistry = new CallbackRegistryImpl();

        var messageSystemRegisterRmi = new MessageSystemRegisterByRmiServer(messageSystem, callbackRegistry);
        var messageSystemRegisterRmiStub = (MessageSystemRegisterByRmi) UnicastRemoteObject.exportObject(messageSystemRegisterRmi, 0);

        var messageSystemRmi = new MessageSystemRmi(messageSystemRegisterRmiStub);

        try {
            HandlersStore requestHandlerDatabaseStore = new HandlersStoreImpl();
            requestHandlerDatabaseStore.addHandler(MessageType.USER_DATA, new RequestHandler<ResultDataType>() {
                @Override
                public Optional<Message> handle(Message msg) {
                    Box box = MessageHelper.getPayload(msg);
                    log.info("get box {}", box);
                    return Optional.of(MessageBuilder.buildReplyMessage(msg, new Box(box.getText() + " " + box.getText())));
                }
            });

            MsClientImpl databaseMsClient = new MsClientImpl(DATABASE_SERVICE_CLIENT_NAME, messageSystemRmi, requestHandlerDatabaseStore, null);
            messageSystem.addClient(databaseMsClient);

            HandlersStoreImpl handler2 = new HandlersStoreImpl();
            handler2.addHandler(MessageType.USER_DATA, new CallbackRequestHandler<>(callbackRegistry));
            MsClientImpl frontendMsClient = new MsClientImpl(FRONTEND_SERVICE_CLIENT_NAME, messageSystem, handler2, callbackRegistry);
            messageSystem.addClient(frontendMsClient);

            int counter = 1;
            CountDownLatch waitLatch = new CountDownLatch(counter);
            Message outMsg = frontendMsClient.produceMessage(DATABASE_SERVICE_CLIENT_NAME, new Box("AAA"),
                    MessageType.USER_DATA, new MessageCallback<Box>() {
                        @Override
                        public void accept(Box box) {
                            assertEquals("AAA AAA", box.getText());
                            waitLatch.countDown();
                        }
                    });
            frontendMsClient.sendMessage(outMsg);
            assertTrue(waitLatch.await(5, TimeUnit.SECONDS));
        } finally {
            messageSystem.dispose();
            UnicastRemoteObject.unexportObject(messageSystemRegisterRmi, true);
        }
    }
}
