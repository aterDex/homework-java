package ru.otus.homework.hw26.data;

import lombok.extern.slf4j.Slf4j;
import org.flywaydb.core.Flyway;

import javax.sql.DataSource;

@Slf4j
public class FlywayUtils {

    private FlywayUtils() {
    }

    public static void flywayMigrations(DataSource dataSource, String scriptPaths) {
        log.info("***************** flywayMigrations *****************");
        var flyway = Flyway.configure()
                .dataSource(dataSource)
                .locations(scriptPaths)
                .load();
        flyway.migrate();
    }
}
