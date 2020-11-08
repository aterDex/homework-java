package ru.otus.homework.hw32.back.config;

import org.flywaydb.core.Flyway;
import org.hibernate.SessionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import ru.otus.homework.hw32.back.data.core.model.User;
import ru.otus.homework.hw32.back.data.hibernate.HibernateUtils;

import javax.sql.DataSource;

@Configuration
public class DataConfig {

    @Bean(initMethod = "migrate")
    public Flyway flyway(DataSource dataSource) {
        return Flyway.configure()
                .dataSource(dataSource)
                .locations("/db/migration")
                .load();
    }

    @Bean
    @DependsOn({"flyway"})
    public SessionFactory sessionFactory(DataSource dataSource) {
        return HibernateUtils.buildSessionFactory("hibernate.cfg.xml", dataSource, User.class);
    }
}