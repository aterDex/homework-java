package ru.otus.homework.hw32.common;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import ru.otus.homework.hw32.common.message.CallbackRequestHandler;
import ru.otus.messagesystem.HandlersStoreImpl;
import ru.otus.messagesystem.MessageSystem;
import ru.otus.messagesystem.client.CallbackRegistryImpl;
import ru.otus.messagesystem.client.MessageCallback;
import ru.otus.messagesystem.client.MsClient;
import ru.otus.messagesystem.client.MsClientImpl;
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
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@Slf4j
public class MessageSystemTest {

    static Collection<MessageSystemStand> getStandsOneSenderOneHandler() {
        var providers = List.of(
                new MessageSystemProviderDirect(),
                new MessageSystemProviderRmi(),
                new MessageSystemProviderSignalTcp("127.0.0.1", 0, Executors.newSingleThreadExecutor()));
        var crossAll = new ArrayList<MessageSystemStand>(providers.size() * providers.size());
        for (int i = 0; i < providers.size(); i++) {
            for (int j = i; j < providers.size(); j++) {
                crossAll.add(new MessageSystemStand("(sender, handler)", List.of(
                        providers.get(i),
                        providers.get(j)
                )));
            }
        }
        return crossAll;
    }

    static Collection<MessageSystemStand> getStandsTwoSenderTwoHandler() {
        String desc = "(sender1, sender2, handler1, handler2)";
        return List.of(
                new MessageSystemStand(desc, List.of(
                        new MessageSystemProviderDirect(),
                        new MessageSystemProviderRmi(),
                        new MessageSystemProviderSignalTcp("127.0.0.1", 0, Executors.newSingleThreadExecutor()),
                        new MessageSystemProviderDirect()
                ))
        );
    }

    @ParameterizedTest
    @MethodSource("getStandsOneSenderOneHandler")
    void CheckMessageSystemIntegrationOneSenderOneHandler(MessageSystemStand stand) throws Exception {
        stand.init();
        try {

            Function<Box, Box> handler = x -> new Box(x.getText() + " " + x.getText());
            MsClient msClientSender = initMsClientSender("sender1", stand.getRemoteMessageSystems().get(0));
            MsClient msClientHandler = initMsClientHandler("handler1", stand.getRemoteMessageSystems().get(1), handler);

            int countThread = 5;
            int countMessage = 10;

            var executors = Executors.newFixedThreadPool(countThread);
            var future = new ArrayList<Future<Throwable>>(countThread);
            for (int i = 0; i < countThread; i++) {
                future.add(executors.submit(() -> sendMessages(countMessage, msClientSender, "handler1", handler)));
            }
            for (Future<Throwable> waiter : future) {
                assertNull(waiter.get());
            }
        } finally {
            stand.disposeAll();
        }
    }

    @ParameterizedTest
    @MethodSource("getStandsTwoSenderTwoHandler")
    void CheckMessageSystemIntegrationTwoSenderTwoHandler(MessageSystemStand stand) throws Exception {
        stand.init();
        try {
            Function<Box, Box> handler1 = x -> new Box(x.getText() + "-" + x.getText());
            Function<Box, Box> handler2 = x -> new Box(x.getText() + "=" + x.getText());
            MsClient msClientSender1 = initMsClientSender("sender1", stand.getRemoteMessageSystems().get(1));
            MsClient msClientSender2 = initMsClientSender("sender2", stand.getRemoteMessageSystems().get(1));
            MsClient msClientHandler1 = initMsClientHandler("handler1", stand.getRemoteMessageSystems().get(0), handler1);
            MsClient msClientHandler2 = initMsClientHandler("handler2", stand.getRemoteMessageSystems().get(0), handler2);

            int countThread = 2;
            int countMessage = 100;

            var executors = Executors.newFixedThreadPool(countThread * 4);
            var future = new ArrayList<Future<Throwable>>(countThread);
            for (int i = 0; i < countThread; i++) {
                future.add(executors.submit(() -> sendMessages(countMessage, msClientSender1, "handler1", handler1)));
            }
            for (int i = 0; i < countThread; i++) {
                future.add(executors.submit(() -> sendMessages(countMessage, msClientSender1, "handler2", handler2)));
            }
            for (int i = 0; i < countThread; i++) {
                future.add(executors.submit(() -> sendMessages(countMessage, msClientSender2, "handler1", handler1)));
            }
            for (int i = 0; i < countThread; i++) {
                future.add(executors.submit(() -> sendMessages(countMessage, msClientSender2, "handler2", handler2)));
            }
            for (Future<Throwable> waiter : future) {
                assertNull(waiter.get());
            }
        } finally {
            stand.disposeAll();
        }
    }

    private MsClient initMsClientSender(String name, MessageSystem messageSystem) {
        var callbackRegistry = new CallbackRegistryImpl();
        var handler = new HandlersStoreImpl();
        handler.addHandler(MessageType.USER_DATA, new CallbackRequestHandler<>(callbackRegistry));
        var client = new MsClientImpl(name, messageSystem, handler, callbackRegistry);
        messageSystem.addClient(client);
        return client;
    }

    private MsClient initMsClientHandler(String name, MessageSystem messageSystem, Function<Box, Box> handler) {
        var requestHandlerDatabaseStore = new HandlersStoreImpl();
        requestHandlerDatabaseStore.addHandler(MessageType.USER_DATA, msg -> {
            Box box = MessageHelper.getPayload(msg);
            return Optional.of(MessageBuilder.buildReplyMessage(msg, handler.apply(box)));
        });
        var client = new MsClientImpl(name, messageSystem, requestHandlerDatabaseStore, null);
        messageSystem.addClient(client);
        return client;
    }

    private Throwable sendMessages(int count, MsClient client, String name, Function<Box, Box> handler) {
        try {
            Exchanger<Box> exchanger = new Exchanger<>();
            for (int i = 0; i < count; i++) {
                Box original = new Box(String.valueOf(i));
                Message outMsg = client.produceMessage(name, original,
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
                assertEquals(handler.apply(original).getText(), box.getText());
            }
            return null;
        } catch (Throwable t) {
            log.warn("", t);
            return t;
        }
    }
}
