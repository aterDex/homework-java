package ru.otus.homework.provoker.impl;

import com.google.common.collect.ImmutableSet;
import com.google.common.reflect.ClassPath;
import ru.otus.homework.provoker.api.Detective;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;

/**
 * Смотрим в пакетах нужные классы
 */
public class DetectiveScanPackageWithFilter implements Detective {

    private String packageName;
    /**
     * В рамках какого класс лоэдера ищем пакеты
     */
    private ClassLoader classloader;

    private Predicate<Class<?>> filter;

    public DetectiveScanPackageWithFilter(String packageName, Predicate<Class<?>> filter) {
        this(packageName, filter,
                Thread.currentThread().getContextClassLoader() == null ?
                        DetectiveScanPackageWithFilter.class.getClassLoader() :
                        Thread.currentThread().getContextClassLoader());
    }

    /**
     * @param packageName пакет где ищем
     * @param filter      фильтр, если null то пропускаем все классы
     * @param classloader в рамках какого класс лоудера ищем
     */
    public DetectiveScanPackageWithFilter(String packageName, Predicate<Class<?>> filter, ClassLoader classloader) {
        this.packageName = packageName;
        this.classloader = classloader;
        this.filter = filter;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public ClassLoader getClassloader() {
        return classloader;
    }

    public void setClassloader(ClassLoader classloader) {
        this.classloader = classloader;
    }

    public Predicate<Class<?>> getFilter() {
        return filter;
    }

    public void setFilter(Predicate<Class<?>> filter) {
        this.filter = filter;
    }

    @Override
    public Collection<Class<?>> search() {
        try {
            ClassPath classPath = ClassPath.from(classloader);
            ImmutableSet<ClassPath.ClassInfo> classes = classPath.getTopLevelClassesRecursive(packageName);
            List<Class<?>> result = new ArrayList<>(classes.size());
            for (ClassPath.ClassInfo classInfo : classes) {
                Class<?> clazz = classInfo.load();
                if (filter == null || filter.test(clazz)) {
                    result.add(clazz);
                }
            }
            return result;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
