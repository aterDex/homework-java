package ru.otus.homework.hw32.common;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import ru.otus.homework.hw32.common.message.CallbackRequestHandler;
import ru.otus.messagesystem.HandlersStore;
import ru.otus.messagesystem.HandlersStoreImpl;
import ru.otus.messagesystem.RequestHandler;
import ru.otus.messagesystem.client.*;
import ru.otus.messagesystem.message.Message;
import ru.otus.messagesystem.message.MessageBuilder;
import ru.otus.messagesystem.message.MessageHelper;
import ru.otus.messagesystem.message.MessageType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Exchanger;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@Slf4j
public class MessageSystemTest {

    private static final String FRONTEND_SERVICE_CLIENT_NAME = "front1";
    private static final String DATABASE_SERVICE_CLIENT_NAME = "back1";

    static Collection<MessageSystemStand> getStands() {
        List<MessageSystemProvider> providers = List.of(
                new MessageSystemProviderDirect(),
                new MessageSystemProviderRmi(),
                new MessageSystemProviderSignalTcp("127.0.0.1", 12000, Executors.newSingleThreadExecutor()));
        List<MessageSystemStand> crossAll = new ArrayList<>(providers.size() * providers.size() - providers.size());
        for (int i = 0; i < providers.size(); i++) {
            for (int j = i; j < providers.size(); j++) {
                if (i == j) {
                    continue;
                }
                crossAll.add(new MessageSystemStand("=", List.of(
                        providers.get(i),
                        providers.get(j)
                )));
                crossAll.add(new MessageSystemStand("=", List.of(
                        providers.get(j),
                        providers.get(i)
                )));
            }
        }

        return crossAll;
    }

    @ParameterizedTest
    @MethodSource("getStands")
    void CheckMessageSystemIntegrationTest(MessageSystemStand stand) throws Exception {

        stand.init();

        var messageSystemFirst = stand.getRemoteMessageSystems().get(0);
        var messageSystemSecond = stand.getRemoteMessageSystems().get(1);

        try {
            HandlersStore requestHandlerDatabaseStore = new HandlersStoreImpl();
            requestHandlerDatabaseStore.addHandler(MessageType.USER_DATA, new RequestHandler<ResultDataType>() {
                @Override
                public Optional<Message> handle(Message msg) {
                    Box box = MessageHelper.getPayload(msg);
                    return Optional.of(MessageBuilder.buildReplyMessage(msg, new Box(box.getText() + " " + box.getText())));
                }
            });

            MsClientImpl databaseMsClient = new MsClientImpl(DATABASE_SERVICE_CLIENT_NAME, messageSystemFirst, requestHandlerDatabaseStore, null);
            messageSystemFirst.addClient(databaseMsClient);

            var callbackRegistry = new CallbackRegistryImpl();

            HandlersStoreImpl handler2 = new HandlersStoreImpl();
            handler2.addHandler(MessageType.USER_DATA, new CallbackRequestHandler<>(callbackRegistry));
            MsClientImpl frontendMsClient = new MsClientImpl(FRONTEND_SERVICE_CLIENT_NAME, messageSystemSecond, handler2, callbackRegistry);
            messageSystemSecond.addClient(frontendMsClient);


            int countThread = 5;
            var executors = Executors.newFixedThreadPool(countThread);
            List<Future<Throwable>> future = new ArrayList<>(countThread);
            for (int i = 0; i < countThread; i++) {
                future.add(executors.submit(() -> sendMessages(10, frontendMsClient)));
            }
            for (Future<Throwable> waiter : future) {
                assertNull(waiter.get());
            }
        } finally {
            stand.disposeAll();
        }
    }

    private Throwable sendMessages(int count, MsClient client) {
        try {
            Exchanger<Box> exchanger = new Exchanger<>();
            for (int i = 0; i < count; i++) {
                Box original = new Box(String.valueOf(i));
                Message outMsg = client.produceMessage(DATABASE_SERVICE_CLIENT_NAME, original,
                        MessageType.USER_DATA, new MessageCallback<Box>() {
                            @Override
                            public void accept(Box box) {
                                try {
                                    exchanger.exchange(box, 3, TimeUnit.SECONDS);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });

                client.sendMessage(outMsg);
                Box box = exchanger.exchange(null, 5, TimeUnit.SECONDS);
                assertEquals(original.getText() + " " + original.getText(), box.getText());
            }
            return null;
        } catch (Throwable t) {
            log.warn("", t);
            return t;
        }
    }
}
