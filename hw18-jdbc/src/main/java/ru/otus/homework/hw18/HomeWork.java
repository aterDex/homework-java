package ru.otus.homework.hw18;

import org.flywaydb.core.Flyway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otus.homework.hw18.core.dao.UserDao;
import ru.otus.homework.hw18.core.model.Account;
import ru.otus.homework.hw18.core.model.User;
import ru.otus.homework.hw18.core.service.DbServiceUserImpl;
import ru.otus.homework.hw18.jdbc.mapper.*;
import ru.otus.homework.hw18.h2.DataSourceH2;
import ru.otus.homework.hw18.jdbc.DbExecutorImpl;
import ru.otus.homework.hw18.jdbc.dao.UserDaoJdbcMapper;
import ru.otus.homework.hw18.jdbc.sessionmanager.SessionManagerJdbc;

import javax.sql.DataSource;
import java.math.BigDecimal;


public class HomeWork {
    private static final Logger logger = LoggerFactory.getLogger(HomeWork.class);

    public static void main(String[] args) {
// Общая часть
        var dataSource = new DataSourceH2();
        flywayMigrations(dataSource);
        var sessionManager = new SessionManagerJdbc(dataSource);
        sessionManager.beginSession();

// Работа с пользователем
        DbExecutorImpl<User> dbExecutor = new DbExecutorImpl<>();
        EntityClassMetaData<User> metaDataUser = new EntityClassMetaDataFromReflection<>(User.class);
        JdbcMapper<User> jdbcMapperUser = new JdbcMapperFromReflection<>(metaDataUser, new EntitySQLMetaDataFromReflection(metaDataUser), dbExecutor);
        UserDao userDao = new UserDaoJdbcMapper(jdbcMapperUser, sessionManager);

// Код дальше должен остаться, т.е. userDao должен использоваться
        var dbServiceUser = new DbServiceUserImpl(userDao);
        var id = dbServiceUser.saveUser(new User(0, "dbServiceUser", 30));
        dbServiceUser.getUser(id).ifPresentOrElse(
                crUser -> logger.info("user created, name: {}", crUser.getName()),
                () -> logger.info("user didn't create")
        );
// Работа со счетом
        EntityClassMetaData<Account> metaDataAccount = new EntityClassMetaDataFromReflection<>(Account.class);
        JdbcMapper<Account> jdbcMapperAccount = new JdbcMapperFromReflection<>(metaDataAccount, new EntitySQLMetaDataFromReflection(metaDataAccount), new DbExecutorImpl<>());
        try {
            sessionManager.beginSession();
            var connection = sessionManager.getCurrentSession().getConnection();
            long idAccount = jdbcMapperAccount.insert(new Account(0, "typeTest", BigDecimal.TEN), connection);
            jdbcMapperAccount.findById(idAccount, connection)
                    .ifPresentOrElse(x -> logger.info("account created: {}", x), () -> logger.info("account didn't create."));
            jdbcMapperAccount.update(new Account(0, "typeTestUpdate", BigDecimal.ONE), connection);
            jdbcMapperAccount.findById(idAccount, connection)
                    .ifPresentOrElse(x -> logger.info("account updated: {}", x), () -> logger.info("account didn't update."));
        } finally {
            sessionManager.close();
        }
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
