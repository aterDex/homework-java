package ru.otus.jdbc.mapper;

import lombok.SneakyThrows;
import ru.otus.core.annotation.Id;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class EntityClassMetaDataFromReflection<T> implements EntityClassMetaData<T> {

    private final Class<T> clazz;
    private final Constructor<T> constructor;
    private final List<Field> fields;
    private final Field idField;
    private final List<Field> fieldsWhitoutId;

    @SneakyThrows
    public EntityClassMetaDataFromReflection(Class<T> clazz) {
        this.clazz = clazz;
        this.constructor = clazz.getConstructor();
        this.fields = List.of(clazz.getDeclaredFields());
        this.idField = searchIdField(fields);
        this.fieldsWhitoutId = fields.stream().filter(x -> !x.equals(idField)).collect(Collectors.toUnmodifiableList());

        this.constructor.setAccessible(true);
        this.fields.forEach(x -> x.setAccessible(true));
    }

    private Field searchIdField(Collection<Field> fields) {
        List<Field> ids = fields.stream().filter(x -> x.isAnnotationPresent(Id.class)).collect(Collectors.toList());
        if (ids.isEmpty()) {
            // TODO ошибку прописать
        }
        if (ids.size() > 1) {
            // TODO ошибку прописать
        }
        return ids.get(0);
    }

    @Override
    public String getName() {
        return clazz.getSimpleName();
    }

    @Override
    public Constructor<T> getConstructor() {
        return constructor;
    }

    @Override
    public Field getIdField() {
        return idField;
    }

    @Override
    public List<Field> getAllFields() {
        return fields;
    }

    @Override
    public List<Field> getFieldsWithoutId() {
        return fieldsWhitoutId;
    }
}
