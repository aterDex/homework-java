package ru.otus.homework.hw32.front.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import ru.otus.homework.hw32.common.dto.UserDto;
import ru.otus.homework.hw32.front.message.FrontendService;

import java.util.Collection;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

@Slf4j
@Controller
public class UserController {

    private final FrontendService frontendService;

    public UserController(FrontendService frontendService) {
        this.frontendService = frontendService;
    }

    @GetMapping({"/users"})
    public String users(Model model) throws Exception {
        model.addAttribute("users", frontendService.getAllUsers());
        return "users";
    }
}
