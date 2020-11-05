package ru.otus.homework.hw32;

import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;

@Slf4j
public class ClientCallbackImpl implements ClientCallback {

//    private static final long serialVersionUID = 227L;

    @Override
    public String workInServer(String text) {
        log.info("work pid {} {}", ProcessHandle.current().pid(), text);
        return text + " " + text;
    }
}
