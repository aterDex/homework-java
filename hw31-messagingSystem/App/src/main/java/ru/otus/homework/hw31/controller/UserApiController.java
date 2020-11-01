package ru.otus.homework.hw31.controller;

import org.springframework.web.bind.annotation.*;
import ru.otus.homework.hw31.data.core.model.User;
import ru.otus.homework.hw31.data.core.service.DBServiceUser;

import java.util.List;

@RestController
@RequestMapping(value = "/api/user")
public class UserApiController {

    private final DBServiceUser dbServiceUser;

    public UserApiController(DBServiceUser dbServiceUser) {
        this.dbServiceUser = dbServiceUser;
    }

    @GetMapping
    public List<User> getUsers() {
        return dbServiceUser.getUsers();
    }

    @PostMapping
    public void addUser(@RequestBody User user) {
        dbServiceUser.saveUser(user);
    }
}
