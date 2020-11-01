package ru.otus.homework.hw31.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import ru.otus.homework.hw31.data.core.model.User;
import ru.otus.homework.hw31.data.core.service.DBServiceUser;

import java.util.List;

@Controller
@Slf4j
public class MessageController {

    private final DBServiceUser dbServiceUser;

    public MessageController(DBServiceUser dbServiceUser) {
        this.dbServiceUser = dbServiceUser;
    }

    @MessageMapping("/addUser")
    @SendTo("/topic/users")
    public List<User> getMessage(User user) {
        dbServiceUser.saveUser(user);
        return dbServiceUser.getUsers();
    }

}
