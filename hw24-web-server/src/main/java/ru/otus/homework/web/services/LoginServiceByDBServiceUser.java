package ru.otus.homework.web.services;

import org.eclipse.jetty.security.AbstractLoginService;
import org.eclipse.jetty.util.security.Password;
import ru.otus.homework.data.core.model.User;
import ru.otus.homework.data.core.service.DBServiceUser;

import java.util.Optional;

public class LoginServiceByDBServiceUser extends AbstractLoginService {

    private final DBServiceUser userServices;
    private final String[] ADMIN_ROLE = {"admin", "user"};
    private final String[] USERS_ROLE = {"user"};

    public LoginServiceByDBServiceUser(DBServiceUser userServices) {
        this.userServices = userServices;
    }

    @Override
    protected String[] loadRoleInfo(UserPrincipal userPrincipal) {
        if (userPrincipal.getName().equals("admin")) {
            return ADMIN_ROLE;
        } else {
            return USERS_ROLE;
        }
    }

    @Override
    protected UserPrincipal loadUserInfo(String login) {
        Optional<User> dbUser = userServices.findByLogin(login);
        return dbUser.map(u -> new UserPrincipal(u.getLogin(), new Password(u.getPassword()))).orElse(null);
    }
}
