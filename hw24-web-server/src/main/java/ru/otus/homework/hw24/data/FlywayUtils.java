package ru.otus.homework.hw24.data;

import lombok.extern.slf4j.Slf4j;
import org.flywaydb.core.Flyway;

@Slf4j
public class FlywayUtils {

    public static void flywayMigrations(String url, String user, String password, String scriptPaths) {
        log.info("***************** flywayMigrations *****************");
        var flyway = Flyway.configure()
                .dataSource(url, user, password)
                .locations(scriptPaths)
                .load();
        flyway.migrate();
    }
}
