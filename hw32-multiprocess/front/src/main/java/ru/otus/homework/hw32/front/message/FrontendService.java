package ru.otus.homework.hw32.front.message;

import ru.otus.homework.hw32.front.data.core.model.User;

import java.util.List;
import java.util.function.Consumer;

public interface FrontendService {

    void saveUser(User user, Consumer<User> dataConsumer, Consumer<ErrorAction> errorConsumer);

    void getAllUsers(Consumer<List<User>> dataConsumer, Consumer<ErrorAction> errorConsumer);
}
