package ru.otus.homework.hw26.config;

import org.h2.jdbcx.JdbcDataSource;
import org.hibernate.SessionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.otus.homework.hw26.data.FlywayUtils;
import ru.otus.homework.hw26.data.core.model.User;
import ru.otus.homework.hw26.data.hibernate.HibernateUtils;

import javax.sql.DataSource;

@Configuration
public class DataConfig {

    private static final String DB_URL = "jdbc:h2:mem:OtusExamplesDB;DB_CLOSE_DELAY=-1";
    private static final String DB_USER = "sa";
    private static final String DB_PASSWORD = "sa";
    private static final String FLY_WAY_SCRIPTS = "classpath:/WEB-INF/db/migration";
    public static final String HIBERNATE_CFG_FILE = "/WEB-INF/hibernate.cfg.xml";

    @Bean
    public DataSource dataSource() {
        var dataSource = new JdbcDataSource();
        dataSource.setUser(DB_USER);
        dataSource.setPassword(DB_PASSWORD);
        dataSource.setURL(DB_URL);
        FlywayUtils.flywayMigrations(dataSource, FLY_WAY_SCRIPTS);
        return dataSource;
    }

    @Bean
    public SessionFactory sessionFactory(DataSource dataSource) {
        return HibernateUtils.buildSessionFactory(HIBERNATE_CFG_FILE, dataSource, User.class);
    }
}
