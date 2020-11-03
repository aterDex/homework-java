package ru.otus.homework.hw31.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import ru.otus.homework.hw31.data.core.model.User;
import ru.otus.homework.hw31.message.FrontendService;

import java.security.Principal;

@Slf4j
@Controller
public class UserWebSocketController {

    private final FrontendService frontendService;
    private final SimpMessagingTemplate template;

    public UserWebSocketController(FrontendService frontendService, SimpMessagingTemplate template) {
        this.frontendService = frontendService;
        this.template = template;
    }

    @MessageMapping("/addUser")
    public void addUser(User user, Principal principal) {
        frontendService.saveUser(
                user,
                us -> template.convertAndSend("/topic/newUser", us),
                er -> template.convertAndSendToUser(principal.getName(), "/queue/errors", er)
        );
    }
}
