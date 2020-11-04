package ru.otus.homework.hw32.front.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import ru.otus.homework.hw32.front.data.core.model.User;
import ru.otus.homework.hw32.front.message.FrontendService;

import java.util.List;
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
        ArrayBlockingQueue<List<User>> blockingQueue = new ArrayBlockingQueue<>(1);
        frontendService.getAllUsers(x -> blockingQueue.add(x), x -> log.error(x.getError()));
        List<User> users = blockingQueue.poll(5, TimeUnit.SECONDS);
        if (users == null) {
            throw new RuntimeException("Не получи данных.");
        }
        model.addAttribute("users", users);
        return "users";
    }
}
