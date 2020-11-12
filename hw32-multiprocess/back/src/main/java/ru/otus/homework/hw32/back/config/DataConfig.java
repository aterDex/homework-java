package ru.otus.homework.hw32.back.config;

import org.flywaydb.core.Flyway;
import org.h2.tools.Server;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Profile;
import ru.otus.homework.hw32.back.data.core.model.User;
import ru.otus.homework.hw32.back.data.hibernate.HibernateUtils;

import javax.sql.DataSource;

@Configuration
public class DataConfig {

    @Value("${server.db.port}")
    private int port;

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

    @Profile("H2_DB_server")
    @Bean(destroyMethod = "shutdown", initMethod = "start")
    public Server dbServer() throws Exception {
        return Server.createTcpServer("-tcpPort", String.valueOf(port), "-tcpDaemon");
    }
}
