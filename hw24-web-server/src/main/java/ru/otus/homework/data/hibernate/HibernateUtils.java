package ru.otus.homework.data.hibernate;

import lombok.SneakyThrows;
import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Arrays;
import java.util.function.Consumer;

public final class HibernateUtils {

    private HibernateUtils() {
    }

    @SneakyThrows
    public static SessionFactory buildSessionFactory(String configResourceFileName, Consumer<Configuration> customSchemaGeneration, Class<?>... annotatedClasses) {
        Configuration configuration = new Configuration().configure(configResourceFileName);
        if (customSchemaGeneration != null) {
            customSchemaGeneration.accept(configuration);
        }

        MetadataSources metadataSources = new MetadataSources(createServiceRegistry(configuration));
        Arrays.stream(annotatedClasses).forEach(metadataSources::addAnnotatedClass);

        Metadata metadata = metadataSources.getMetadataBuilder().build();
        return metadata.getSessionFactoryBuilder().build();
    }

    private static StandardServiceRegistry createServiceRegistry(Configuration configuration) {
        return new StandardServiceRegistryBuilder()
                .applySettings(configuration.getProperties()).build();
    }
}
