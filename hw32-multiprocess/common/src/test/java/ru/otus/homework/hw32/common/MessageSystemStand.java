package ru.otus.homework.hw32.common;

import lombok.extern.slf4j.Slf4j;
import ru.otus.messagesystem.MessageSystem;
import ru.otus.messagesystem.MessageSystemImpl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class MessageSystemStand {

    private final String description;
    private final Collection<MessageSystemProvider> providers;
    private final List<DisposableMessageSystem> disposableMessageSystems;
    private final List<MessageSystem> messageSystems;
    private final List<MessageSystem> unmodifiableMessageSystems;

    private MessageSystem messageSystemCore;

    public MessageSystemStand(String description, Collection<MessageSystemProvider> providers) {
        this.description = String.format("%s [%s]", description, providers.stream()
                .map(MessageSystemProvider::getDescription)
                .collect(Collectors.joining(", ")));
        this.providers = Collections.unmodifiableCollection(new ArrayList<>(providers));
        this.disposableMessageSystems = new ArrayList<>(this.providers.size());
        this.messageSystems = new ArrayList<>(this.providers.size());
        this.unmodifiableMessageSystems = Collections.unmodifiableList(messageSystems);
    }

    public void init() throws Exception {
        if (messageSystemCore != null) {
            throw new RuntimeException("You mustn't invoke init method again!");
        }
        messageSystemCore = new MessageSystemImpl();

        for (MessageSystemProvider provider : providers) {
            log.info("init provider [{}]", provider.getDescription());
            DisposableMessageSystem ms = provider.init(messageSystemCore);
            disposableMessageSystems.add(ms);
            messageSystems.add(ms.getMessageSystem());
        }
    }

    public void disposeAll() throws Exception {
        for (DisposableMessageSystem disposableMessageSystem : disposableMessageSystems) {
            log.info("dispose [{}]", disposableMessageSystem.getDescription());
            try {
                disposableMessageSystem.getDispose().run();
            } catch (Exception e) {
                log.info("", e);
            }
        }
        messageSystemCore.dispose();
    }

    public MessageSystem getCore() {
        return messageSystemCore;
    }

    public List<MessageSystem> getRemoteMessageSystems() {
        return unmodifiableMessageSystems;
    }

    @Override
    public String toString() {
        return description;
    }
}
