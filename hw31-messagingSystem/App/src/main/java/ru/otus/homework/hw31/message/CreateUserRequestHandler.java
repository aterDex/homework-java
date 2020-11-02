package ru.otus.homework.hw31.message;

import ru.otus.homework.hw31.data.core.model.User;
import ru.otus.homework.hw31.data.core.service.DBServiceUser;
import ru.otus.messagesystem.RequestHandler;
import ru.otus.messagesystem.message.Message;
import ru.otus.messagesystem.message.MessageBuilder;
import ru.otus.messagesystem.message.MessageHelper;

import java.util.Optional;

public class CreateUserRequestHandler implements RequestHandler<User> {

    private final DBServiceUser dbService;

    public CreateUserRequestHandler(DBServiceUser dbService) {
        this.dbService = dbService;
    }

    @Override
    public Optional<Message> handle(Message msg) {
        User user = MessageHelper.getPayload(msg);
        try {
            dbService.saveUser(user);
        } catch (Exception e) {
            return Optional.of(MessageBuilder.buildReplyMessage(msg, new ErrorAction(String.format("Ошибка при создании пользователя %s. %s", user, e.getMessage()))));
        }
        return Optional.of(MessageBuilder.buildReplyMessage(msg, user));
    }
}
