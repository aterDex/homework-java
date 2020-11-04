package ru.otus.homework.hw32.front.message;

import lombok.extern.slf4j.Slf4j;
import ru.otus.homework.hw32.front.data.core.model.User;
import ru.otus.messagesystem.client.MessageCallback;
import ru.otus.messagesystem.client.MsClient;
import ru.otus.messagesystem.client.ResultDataType;
import ru.otus.messagesystem.message.Message;
import ru.otus.messagesystem.message.MessageType;

import java.util.List;
import java.util.function.Consumer;

@Slf4j
public class FrontendServiceRpc implements FrontendService {

    private final String databaseServiceClientName;

    public FrontendServiceRpc(String databaseServiceClientName) {
        this.databaseServiceClientName = databaseServiceClientName;
    }

    @Override
    public void saveUser(User user, Consumer<User> dataConsumer, Consumer<ErrorAction> errorConsumer) {
//        Message outMsg = msClient.produceMessage(databaseServiceClientName, user,
//                MessageType.USER_DATA, (MessageCallback<ResultDataType>) data -> {
//                    if (data instanceof ErrorAction) {
//                        errorConsumer.accept((ErrorAction) data);
//                    } else if (data instanceof User) {
//                        dataConsumer.accept((User) data);
//                    } else {
//                        log.error("Unknown data {}.", data);
//                    }
//                });
//        msClient.sendMessage(outMsg);
    }

    @Override
    public void getAllUsers(Consumer<List<User>> dataConsumer, Consumer<ErrorAction> errorConsumer) {
    }
}
