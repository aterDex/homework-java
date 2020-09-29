package ru.otus.homework.hw22.cache.service;

import lombok.extern.slf4j.Slf4j;
import ru.otus.homework.hw22.cachehw.HwCache;
import ru.otus.homework.hw22.core.model.User;
import ru.otus.homework.hw22.core.service.DBServiceUser;

import java.util.Optional;

@Slf4j
public class DBServiceUserCache implements DBServiceUser {

    private final DBServiceUser serviceUser;
    private final HwCache<String, User> cache;

    public DBServiceUserCache(DBServiceUser serviceUser, HwCache<String, User> cache) {
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
        long id = serviceUser.saveUser(user);
        cache.put(String.valueOf(user.getId()), user);
        return id;
    }

    @Override
    public Optional<User> getUser(long id) {
        User value = cache.get(String.valueOf(id));
        if (value != null) {
            return Optional.of(value);
        }
        Optional<User> valueOpt = serviceUser.getUser(id);
        if (valueOpt.isPresent()) {
            cache.put(String.valueOf(id), valueOpt.get());
        }
        return valueOpt;
    }
}
