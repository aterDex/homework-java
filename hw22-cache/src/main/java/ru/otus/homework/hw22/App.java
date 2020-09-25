package ru.otus.homework.hw22;

import lombok.extern.slf4j.Slf4j;
import org.flywaydb.core.Flyway;
import ru.otus.homework.hw22.core.model.Account;
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
import java.math.BigDecimal;

@Slf4j
public class App {

    public static void main(String[] args) {
// Общая часть
        var dataSource = new DataSourceH2();
        flywayMigrations(dataSource);
        var sessionManager = new SessionManagerJdbc(dataSource);
        sessionManager.beginSession();

// Работа с пользователем
        var dbExecutor = new DbExecutorImpl<User>();
        var metaDataUser = new EntityClassMetaDataFromReflection<>(User.class);
        var jdbcMapperUser = new JdbcMapperFromReflection<>(metaDataUser, new EntitySQLMetaDataFromReflection(metaDataUser), dbExecutor);
        var userDao = new UserDaoJdbcMapper(jdbcMapperUser, sessionManager);

// Код дальше должен остаться, т.е. userDao должен использоваться
        var dbServiceUser = new DbServiceUserImpl(userDao);
        var id = dbServiceUser.saveUser(new User(0, "dbServiceUser", 30));
        dbServiceUser.getUser(id).ifPresentOrElse(
                crUser -> log.info("user created, name: {}", crUser.getName()),
                () -> log.info("user didn't create")
        );
// Работа со счетом
        var metaDataAccount = new EntityClassMetaDataFromReflection<>(Account.class);
        var jdbcMapperAccount = new JdbcMapperFromReflection<>(metaDataAccount, new EntitySQLMetaDataFromReflection(metaDataAccount), new DbExecutorImpl<>());
        try {
            sessionManager.beginSession();
            var connection = sessionManager.getCurrentSession().getConnection();
            var idAccount = jdbcMapperAccount.insert(new Account(0, "typeTest", BigDecimal.TEN), connection);
            jdbcMapperAccount.findById(idAccount, connection)
                    .ifPresentOrElse(x -> log.info("account created: {}", x), () -> log.info("account didn't create."));
            jdbcMapperAccount.update(new Account(0, "typeTestUpdate", BigDecimal.ONE), connection);
            jdbcMapperAccount.findById(idAccount, connection)
                    .ifPresentOrElse(x -> log.info("account updated: {}", x), () -> log.info("account didn't update."));
        } finally {
            sessionManager.close();
        }
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
