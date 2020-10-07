package ru.otus.homework.hw26.config;

import org.h2.jdbcx.JdbcDataSource;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.otus.homework.hw26.data.FlywayUtils;
import ru.otus.homework.hw26.data.core.model.User;
import ru.otus.homework.hw26.data.hibernate.HibernateUtils;

import javax.sql.DataSource;

@Configuration
public class DataConfig {

    @Value("${db.url}")
    private String dbUrl;

    @Value("${db.user}")
    private String dbUser;

    @Value("${db.password}")
    private String dbPassword;

    @Value("${flyway.scripts}")
    private String flywayScripts;

    @Value("${hibernate.config}")
    private String hibernateConfig;

    @Bean
    public DataSource dataSource() {
        var dataSource = new JdbcDataSource();
        dataSource.setUser(dbUser);
        dataSource.setPassword(dbPassword);
        dataSource.setURL(dbUrl);
        FlywayUtils.flywayMigrations(dataSource, flywayScripts);
        return dataSource;
    }

    @Bean
    public SessionFactory sessionFactory(DataSource dataSource) {
        return HibernateUtils.buildSessionFactory(hibernateConfig, dataSource, User.class);
    }
}
