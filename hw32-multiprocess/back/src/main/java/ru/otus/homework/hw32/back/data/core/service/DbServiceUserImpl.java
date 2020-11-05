package ru.otus.homework.hw32.back.data.core.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.otus.homework.hw32.back.data.core.dao.UserDao;
import ru.otus.homework.hw32.back.data.core.model.User;
import ru.otus.homework.hw32.back.data.core.sessionmanager.SessionManager;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

@Slf4j
@Service
public class DbServiceUserImpl implements DBServiceUser {

    private final UserDao userDao;

    public DbServiceUserImpl(UserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    public long saveUser(User user) {
        try (SessionManager sessionManager = userDao.getSessionManager()) {
            sessionManager.beginSession();
            try {
                userDao.insertOrUpdate(user);
                long userId = user.getId();
                sessionManager.commitSession();

                log.info("created user: {}", userId);
                return userId;
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                sessionManager.rollbackSession();
                throw new DbServiceException(e);
            }
        }
    }

    @Override
    public Optional<User> getUser(long id) {
        return invokeIntoSession(() -> userDao.findById(id), Optional.empty());
    }

    @Override
    public List<User> getUsers() {
        return invokeIntoSession(userDao::getUsers, Collections.emptyList());
    }

    @Override
    public Optional<User> findByLogin(String login) {
        return invokeIntoSession(() -> userDao.findByLogin(login), Optional.empty());
    }

    private <R> R invokeIntoSession(Supplier<R> run, R def) {
        try (var sessionManager = userDao.getSessionManager()) {
            sessionManager.beginSession();
            try {
                return run.get();
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                sessionManager.rollbackSession();
            }
            return def;
        }
    }
}
