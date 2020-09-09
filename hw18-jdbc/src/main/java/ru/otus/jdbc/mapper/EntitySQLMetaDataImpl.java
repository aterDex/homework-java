package ru.otus.jdbc.mapper;

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
        this.selectAllSql = null;
        this.selectByIdSql = null;
        var l = (List<Field>) entityClassMetaData.getAllFields();
        this.insertSql = String.format("insert into %s (%s) values (%s)",
                entityClassMetaData.getName(),
                l.stream().map(x -> x.getName()).collect(Collectors.joining(", ")),
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
