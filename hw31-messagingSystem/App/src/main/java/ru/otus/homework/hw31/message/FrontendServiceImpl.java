package ru.otus.homework.hw31.message;

import lombok.extern.slf4j.Slf4j;
import ru.otus.homework.hw31.data.core.model.User;
import ru.otus.messagesystem.client.MessageCallback;
import ru.otus.messagesystem.client.MsClient;
import ru.otus.messagesystem.client.ResultDataType;
import ru.otus.messagesystem.message.Message;
import ru.otus.messagesystem.message.MessageType;

@Slf4j
public class FrontendServiceImpl implements FrontendService {

    private final MsClient msClient;
    private final String databaseServiceClientName;

    public FrontendServiceImpl(MsClient msClient, String databaseServiceClientName) {
        this.msClient = msClient;
        this.databaseServiceClientName = databaseServiceClientName;
    }

    @Override
    public void saveUser(User user, MessageCallback<User> dataConsumer, MessageCallback<ErrorAction> errorConsumer) {
        Message outMsg = msClient.produceMessage(databaseServiceClientName, user,
                MessageType.USER_DATA, (MessageCallback<ResultDataType>) data -> {
                    if (data instanceof ErrorAction) {
                        errorConsumer.accept((ErrorAction) data);
                    } else if (data instanceof User) {
                        dataConsumer.accept((User) data);
                    } else {
                        log.error("Unknown data {}.", data);
                    }
                });
        msClient.sendMessage(outMsg);
    }
}
