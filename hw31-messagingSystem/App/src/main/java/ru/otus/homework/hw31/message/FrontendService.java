package ru.otus.homework.hw31.message;

import ru.otus.homework.hw31.data.core.model.User;
import ru.otus.messagesystem.client.MessageCallback;

public interface FrontendService {

    void saveUser(User user, MessageCallback<User> dataConsumer, MessageCallback<ErrorAction> errorConsumer);
}
