package ru.otus.homework.hw22;

import lombok.extern.slf4j.Slf4j;
import org.flywaydb.core.Flyway;
import ru.otus.homework.hw22.cache.service.DBServiceUserCache;
import ru.otus.homework.hw22.cachehw.HwListenerLogger;
import ru.otus.homework.hw22.cachehw.MyCache;
import ru.otus.homework.hw22.core.model.User;
import ru.otus.homework.hw22.core.service.DbServiceUserImpl;
import ru.otus.homework.hw22.h2.DataSourceH2;
import ru.otus.homework.hw22.jdbc.DbExecutorImpl;
import ru.otus.homework.hw22.jdbc.dao.UserDaoJdbcMapper;
import ru.otus.homework.hw22.jdbc.mapper.EntityClassMetaDataFromReflection;
import ru.otus.homework.hw22.jdbc.mapper.EntitySQLMetaDataFromReflection;
import ru.otus.homework.hw22.jdbc.mapper.JdbcMapperFromReflection;
import ru.otus.homework.hw22.jdbc.sessionmanager.SessionManagerJdbc;

import javax.sql.DataSource;

@Slf4j
public class App {

    public static void main(String[] args) {

        var dataSource = new DataSourceH2();
        flywayMigrations(dataSource);
        var sessionManager = new SessionManagerJdbc(dataSource);
        sessionManager.beginSession();

        var dbExecutor = new DbExecutorImpl<User>();
        var metaDataUser = new EntityClassMetaDataFromReflection<>(User.class);
        var jdbcMapperUser = new JdbcMapperFromReflection<>(metaDataUser, new EntitySQLMetaDataFromReflection(metaDataUser), dbExecutor);
        var userDao = new UserDaoJdbcMapper(jdbcMapperUser, sessionManager);

        var cacheUser = new MyCache<Long, User>();
        var cacheListenerUser = new HwListenerLogger<Long, User>();
        cacheUser.addListener(cacheListenerUser);

        var dbServiceUser = new DBServiceUserCache(new DbServiceUserImpl(userDao), cacheUser);
        var id = dbServiceUser.saveUser(new User(0, "dbServiceUser", 30));
        dbServiceUser.getUser(id).ifPresentOrElse(
                crUser -> log.info("user created, name: {}", crUser.getName()),
                () -> log.info("user didn't create")
        );
        dbServiceUser.getUser(id).ifPresentOrElse(
                crUser -> log.info("user created, name: {}", crUser.getName()),
                () -> log.info("user didn't create")
        );
    }

    private static void flywayMigrations(DataSource dataSource) {
        log.info("db migration started...");
        var flyway = Flyway.configure()
                .dataSource(dataSource)
                .locations("classpath:/db/migration")
                .load();
        flyway.migrate();
        log.info("db migration finished.");
        log.info("***");
    }
}
