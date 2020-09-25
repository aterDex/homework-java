package ru.otus.homework.hw22.cache.service;

import lombok.extern.slf4j.Slf4j;
import ru.otus.homework.hw22.cachehw.HwCache;
import ru.otus.homework.hw22.core.model.User;
import ru.otus.homework.hw22.core.service.DBServiceUser;

import java.util.Optional;

@Slf4j
public class DBServiceUserCache implements DBServiceUser {

    private final DBServiceUser serviceUser;
    private final HwCache<Long, User> cache;

    public DBServiceUserCache(DBServiceUser serviceUser, HwCache<Long, User> cache) {
        if (serviceUser == null) {
            throw new IllegalArgumentException("serviceUser mustn't be null.");
        }
        if (cache == null) {
            throw new IllegalArgumentException("cache mustn't be null.");
        }
        this.serviceUser = serviceUser;
        this.cache = cache;
    }

    @Override
    public long saveUser(User user) {
        return serviceUser.saveUser(user);
    }

    @Override
    public Optional<User> getUser(long id) {
        User value = cache.get(id);
        if (value != null) {
            return Optional.of(value);
        }
        Optional<User> valueOpt = serviceUser.getUser(id);
        if (valueOpt.isPresent()) {
            cache.put(id, valueOpt.get());
        }
        return valueOpt;
    }
}
