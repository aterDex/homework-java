package ru.otus.homework.hw18.jdbc.mapper;

import java.lang.reflect.Field;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class EntitySQLMetaDataFromReflection implements EntitySQLMetaData {

    private final String selectAllSql;
    private final String selectByIdSql;
    private final String insertSql;
    private final String updateSql;

    public EntitySQLMetaDataFromReflection(EntityClassMetaData<?> entityClassMetaData) {
        String columns = entityClassMetaData.getAllFields().stream()
                .map(Field::getName)
                .collect(Collectors.joining(", "));
        String columnsParameters = Stream
                .generate(() -> "?")
                .limit(entityClassMetaData.getAllFields().size())
                .collect(Collectors.joining(", "));
        String columnsWithoutIdForUpdate = entityClassMetaData.getFieldsWithoutId().stream()
                .map(x -> x.getName() + " = ?")
                .collect(Collectors.joining(", "));

        this.selectAllSql = String.format("select %s from %s", columns, entityClassMetaData.getName());
        this.selectByIdSql = String.format("select %s from %s where %s = ?",
                columns,
                entityClassMetaData.getName(),
                entityClassMetaData.getIdField().getName()
        );
        this.insertSql = String.format("insert into %s (%s) values (%s)",
                entityClassMetaData.getName(),
                columns,
                columnsParameters
        );
        this.updateSql = String.format("update %s set %s where %s = ?",
                entityClassMetaData.getName(),
                columnsWithoutIdForUpdate,
                entityClassMetaData.getIdField().getName()
        );
    }

    @Override
    public String getSelectAllSql() {
        return selectAllSql;
    }

    @Override
    public String getSelectByIdSql() {
        return selectByIdSql;
    }

    @Override
    public String getInsertSql() {
        return insertSql;
    }

    @Override
    public String getUpdateSql() {
        return updateSql;
    }
}
