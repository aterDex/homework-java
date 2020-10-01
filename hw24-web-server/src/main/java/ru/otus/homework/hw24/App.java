package ru.otus.homework.hw24;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import ru.otus.homework.data.core.model.User;
import ru.otus.homework.data.core.service.DbServiceUserImpl;
import ru.otus.homework.data.hibernate.HibernateUtils;
import ru.otus.homework.data.hibernate.dao.UserDaoHibernate;
import ru.otus.homework.data.hibernate.sessionmanager.SessionManagerHibernate;
import ru.otus.homework.web.server.UsersWebServerWithBasicSecurity;
import ru.otus.homework.web.services.LoginServiceByDBServiceUser;
import ru.otus.homework.web.services.TemplateProcessorImpl;

public class App {

    public static final String HIBERNATE_CFG_FILE = "hibernate.cfg.xml";
    private static final int WEB_SERVER_PORT = 8080;
    private static final String TEMPLATES_DIR = "/html/templates/";

    public static void main(String[] args) throws Exception {
        var sessionFactory = HibernateUtils.buildSessionFactory(HIBERNATE_CFG_FILE, User.class);
        try (var sessionManager = new SessionManagerHibernate(sessionFactory)) {

            var dbServiceUser = new DbServiceUserImpl(new UserDaoHibernate(sessionManager));

            initBaseLogin(dbServiceUser);

            Gson gson = new GsonBuilder().serializeNulls().setPrettyPrinting().create();
            var templateProcessor = new TemplateProcessorImpl(TEMPLATES_DIR);
            var loginService = new LoginServiceByDBServiceUser(dbServiceUser);

            var usersWebServer = new UsersWebServerWithBasicSecurity(WEB_SERVER_PORT,
                    loginService, dbServiceUser, gson, templateProcessor);

            usersWebServer.start();
            usersWebServer.join();
        }
    }

    private static void initBaseLogin(DbServiceUserImpl dbServiceUser) {

        var admin = new User();
        admin.setLogin("admin");
        admin.setPassword("password");
        admin.setName("Администратор");
        dbServiceUser.saveUser(admin);

        var user = new User();
        user.setLogin("user");
        user.setPassword("123");
        user.setName("Пользователь");
        dbServiceUser.saveUser(user);
    }
}
