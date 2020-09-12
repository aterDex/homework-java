package ru.otus.hw18.jdbc.mapper;

import java.lang.reflect.Field;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class EntitySQLMetaDataImpl implements EntitySQLMetaData {

    private final String selectAllSql;
    private final String selectByIdSql;
    private final String insertSql;
    private final String updateSql;

    public EntitySQLMetaDataImpl(EntityClassMetaData entityClassMetaData) {
        var l = (List<Field>) entityClassMetaData.getAllFields();
        String columns = l.stream().map(x -> x.getName()).collect(Collectors.joining(", "));
        this.selectAllSql = null;
        this.selectByIdSql = String.format("select %s from %s where %s = ?",
                columns,
                entityClassMetaData.getName(),
                entityClassMetaData.getIdField().getName()
        );
        this.insertSql = String.format("insert into %s (%s) values (%s)",
                entityClassMetaData.getName(),
                columns,
                Stream.generate(() -> "?").limit(l.size()).collect(Collectors.joining(", "))
        );
        this.updateSql = null;
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
