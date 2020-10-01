package ru.otus.homework.web.services;

import org.eclipse.jetty.security.AbstractLoginService;
import org.eclipse.jetty.util.security.Password;
import ru.otus.homework.data.core.service.DBServiceUser;

public class LoginServiceByDBServiceUser extends AbstractLoginService {

    private static final String[] ADMIN_ROLE = {"admin", "user"};
    private static final String[] USERS_ROLE = {"user"};

    private final DBServiceUser userServices;

    public LoginServiceByDBServiceUser(DBServiceUser userServices) {
        if (userServices == null) {
            throw new IllegalArgumentException("userServices mustn't be null.");
        }
        this.userServices = userServices;
    }

    @Override
    protected String[] loadRoleInfo(UserPrincipal userPrincipal) {
        return "admin".equals(userPrincipal.getName()) ? ADMIN_ROLE : USERS_ROLE;
    }

    @Override
    protected UserPrincipal loadUserInfo(String login) {
        return userServices.findByLogin(login)
                .map(u -> new UserPrincipal(u.getLogin(), new Password(u.getPassword())))
                .orElse(null);
    }
}
