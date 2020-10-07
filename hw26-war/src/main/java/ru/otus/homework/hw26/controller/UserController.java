package ru.otus.homework.hw26.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UserController {

    @GetMapping({"/users"})
    public String users(Model model) {
//        List<User> users = usersService.findAll();
//        model.addAttribute("users", users);
        return "users.html";
    }
}
