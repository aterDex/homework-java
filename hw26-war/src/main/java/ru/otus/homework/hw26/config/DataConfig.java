package ru.otus.homework.hw26.config;

import org.flywaydb.core.Flyway;
import org.h2.jdbcx.JdbcDataSource;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import ru.otus.homework.hw26.data.core.model.User;
import ru.otus.homework.hw26.data.hibernate.HibernateUtils;

import javax.sql.DataSource;

@Configuration
public class DataConfig {

    @Value("${db.url}")
    String dbUrl;

    @Value("${db.user}")
    String dbUser;

    @Value("${db.password}")
    String dbPassword;

    @Value("${flyway.scripts}")
    String flywayScripts;

    @Value("${hibernate.config}")
    String hibernateConfig;

    @Bean
    public DataSource dataSource() {
        var dataSource = new JdbcDataSource();
        dataSource.setUser(dbUser);
        dataSource.setPassword(dbPassword);
        dataSource.setURL(dbUrl);
        return dataSource;
    }

    @Bean(initMethod = "migrate")
    public Flyway flyway(DataSource dataSource) {
        return Flyway.configure()
                .dataSource(dataSource)
                .locations(flywayScripts)
                .load();
    }

    @Bean
    @DependsOn({"flyway"})
    public SessionFactory sessionFactory(DataSource dataSource) {
        return HibernateUtils.buildSessionFactory(hibernateConfig, dataSource, User.class);
    }
}
