package ru.otus.homework.hw24;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.h2.jdbcx.JdbcDataSource;
import ru.otus.homework.hw24.data.FlywayUtils;
import ru.otus.homework.hw24.data.core.model.User;
import ru.otus.homework.hw24.data.core.service.DbServiceUserImpl;
import ru.otus.homework.hw24.data.hibernate.HibernateUtils;
import ru.otus.homework.hw24.data.hibernate.dao.UserDaoHibernate;
import ru.otus.homework.hw24.data.hibernate.sessionmanager.SessionManagerHibernate;
import ru.otus.homework.hw24.web.server.UsersWebServerWithBasicSecurity;
import ru.otus.homework.hw24.web.services.LoginServiceByDBServiceUser;
import ru.otus.homework.hw24.web.services.TemplateProcessorImpl;

import javax.sql.DataSource;

public class App {

    public static final String HIBERNATE_CFG_FILE = "hibernate.cfg.xml";
    private static final int WEB_SERVER_PORT = 8080;
    private static final String TEMPLATES_DIR = "/html/templates/";
    private static final String FLY_WAY_SCRIPTS = "classpath:/db/migration";
    private static final String DB_URL = "jdbc:h2:mem:OtusExamplesDB;DB_CLOSE_DELAY=-1";
    private static final String DB_USER = "sa";
    private static final String DB_PASSWORD = "sa";

    public static void main(String[] args) throws Exception {
        var dataSource = initDataSource();

        FlywayUtils.flywayMigrations(dataSource, FLY_WAY_SCRIPTS);

        var sessionFactory = HibernateUtils.buildSessionFactory(HIBERNATE_CFG_FILE, dataSource, User.class);
        try (var sessionManager = new SessionManagerHibernate(sessionFactory)) {

            var dbServiceUser = new DbServiceUserImpl(new UserDaoHibernate(sessionManager));

            Gson gson = new GsonBuilder().serializeNulls().setPrettyPrinting().create();
            var templateProcessor = new TemplateProcessorImpl(TEMPLATES_DIR);
            var loginService = new LoginServiceByDBServiceUser(dbServiceUser);

            var usersWebServer = new UsersWebServerWithBasicSecurity(WEB_SERVER_PORT,
                    loginService, dbServiceUser, gson, templateProcessor);

            usersWebServer.start();
            usersWebServer.join();
        }
    }

    private static DataSource initDataSource() {
        var dataSource = new JdbcDataSource();
        dataSource.setURL(DB_URL);
        dataSource.setPassword(DB_USER);
        dataSource.setUser(DB_PASSWORD);
        return dataSource;
    }
}
