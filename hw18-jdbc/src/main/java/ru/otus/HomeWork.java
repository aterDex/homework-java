package ru.otus;

import org.flywaydb.core.Flyway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otus.core.dao.UserDao;
import ru.otus.core.model.User;
import ru.otus.core.service.DbServiceUserImpl;
import ru.otus.h2.DataSourceH2;
import ru.otus.jdbc.DbExecutorImpl;
import ru.otus.jdbc.mapper.*;
import ru.otus.jdbc.sessionmanager.SessionManagerJdbc;

import javax.sql.DataSource;
import java.util.Optional;


public class HomeWork {
    private static final Logger logger = LoggerFactory.getLogger(HomeWork.class);

    public static void main(String[] args) {
// Общая часть
        var dataSource = new DataSourceH2();
        flywayMigrations(dataSource);
        var sessionManager = new SessionManagerJdbc(dataSource);

// Работа с пользователем
        DbExecutorImpl<User> dbExecutor = new DbExecutorImpl<>();
        EntityClassMetaData<User> metaDataUser = new EntityClassMetaDataFromReflection<>(User.class);
        JdbcMapper<User> jdbcMapperUser = new JdbcMapperImpl(metaDataUser, new EntitySQLMetaDataImpl(metaDataUser), dbExecutor, dataSource);
        jdbcMapperUser.insert(new User(0, "test", 40));
        UserDao userDao = null; // = new UserDaoJdbcMapper(sessionManager, dbExecutor);

//// Код дальше должен остаться, т.е. userDao должен использоваться
//        var dbServiceUser = new DbServiceUserImpl(userDao);
//        var id = dbServiceUser.saveUser(new User(0, "dbServiceUser"));
//        Optional<User> user = dbServiceUser.getUser(id);
//
//        user.ifPresentOrElse(
//                crUser -> logger.info("created user, name:{}", crUser.getName()),
//                () -> logger.info("user was not created")
//        );
//// Работа со счетом


    }

    private static void flywayMigrations(DataSource dataSource) {
        logger.info("db migration started...");
        var flyway = Flyway.configure()
                .dataSource(dataSource)
                .locations("classpath:/db/migration")
                .load();
        flyway.migrate();
        logger.info("db migration finished.");
        logger.info("***");
    }
}
