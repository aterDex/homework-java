package ru.otus.homework.hw26.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import ru.otus.homework.hw26.data.core.model.User;
import ru.otus.homework.hw26.data.core.service.DBServiceUser;

import java.util.List;

@Controller
public class UserController {

    private final DBServiceUser dbServiceUser;

    public UserController(DBServiceUser dbServiceUser) {
        this.dbServiceUser = dbServiceUser;
    }

    @GetMapping({"/users"})
    public String users(Model model) {
        List<User> users = dbServiceUser.getUsers();
        model.addAttribute("users", users);
        return "users.html";
    }
}
