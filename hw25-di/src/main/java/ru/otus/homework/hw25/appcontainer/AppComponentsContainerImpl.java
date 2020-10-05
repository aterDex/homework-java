package ru.otus.homework.hw25.appcontainer;

import lombok.extern.slf4j.Slf4j;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import ru.otus.homework.hw25.appcontainer.api.AppComponent;
import ru.otus.homework.hw25.appcontainer.api.AppComponentsContainer;
import ru.otus.homework.hw25.appcontainer.api.AppComponentsContainerConfig;
import ru.otus.homework.hw25.appcontainer.api.AppComponentsContainerException;

import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
public class AppComponentsContainerImpl implements AppComponentsContainer {

    private final List<Object> appComponents = new ArrayList<>();
    private final Map<String, Object> appComponentsByName = new HashMap<>();

    public AppComponentsContainerImpl(Class<?> initialConfigClass) {
        checkConfigClass(initialConfigClass);
        processConfig(initialConfigClass);
    }

    public AppComponentsContainerImpl(String pckg) {
        this(new Reflections(pckg, new TypeAnnotationsScanner(), new SubTypesScanner())
                .getTypesAnnotatedWith(AppComponentsContainerConfig.class)
                .toArray(Class<?>[]::new));
    }

    public AppComponentsContainerImpl(Class<?>... initialConfigClasses) {
        for (Class<?> initialConfigClass : initialConfigClasses) {
            checkConfigClass(initialConfigClass);
        }
        Arrays.stream(initialConfigClasses)
                .sorted(Comparator.comparingInt(x -> x.getAnnotation(AppComponentsContainerConfig.class).order()))
                .forEachOrdered(this::processConfig);
    }

    private void processConfig(Class<?> configClass) {
        log.info("processConfig");
        final Object instance;
        try {
            var constructor = configClass.getConstructor();
            constructor.setAccessible(true);
            instance = constructor.newInstance();
        } catch (Exception e) {
            throw new AppComponentsContainerException(String.format("Не смогли создать экземпляр класса '%s'.", configClass.getClass().getCanonicalName()), e);
        }
        Method[] methods = configClass.getDeclaredMethods();
        Arrays.stream(methods)
                .filter(x -> x.isAnnotationPresent(AppComponent.class))
                .sorted(Comparator.comparingInt(x -> x.getAnnotation(AppComponent.class).order()))
                .forEachOrdered(x -> invokeMethodForComponent(x, instance));
    }

    private void invokeMethodForComponent(Method method, Object instance) {
        var component = method.getAnnotation(AppComponent.class);
        if (appComponentsByName.containsKey(component.name())) {
            throw new AppComponentsContainerException(String.format("Компонент с именем '%s' уже находится в контексте. В контексте не может быть компонентов с одинаковыми именами.", component.name()));
        }
        try {
            method.setAccessible(true);
            var instanceComponent = method.invoke(instance, resolveArgument(method));
            appComponents.add(instanceComponent);
            appComponentsByName.put(component.name(), instanceComponent);
        } catch (Exception e) {
            throw new AppComponentsContainerException(String.format("Не смогли создать компонент '%s'.", component.name()), e);
        }
    }

    private Object[] resolveArgument(Method method) {
        if (method.getParameterCount() == 0) {
            return null;
        }
        Object[] args = new Object[method.getParameterCount()];
        var parameters = method.getParameters();
        for (int i = 0; i < args.length; i++) {
            args[i] = getAppComponent(parameters[i].getType());
        }
        return args;
    }

    private void checkConfigClass(Class<?> configClass) {
        if (!configClass.isAnnotationPresent(AppComponentsContainerConfig.class)) {
            throw new IllegalArgumentException(String.format("Переданный класс не является конфигурацией '%s'", configClass.getName()));
        }
    }

    @Override
    public <C> C getAppComponent(Class<C> componentClass) {
        List<Object> components = appComponents.stream().filter(x -> componentClass.isInstance(x))
                .collect(Collectors.toList());
        if (components.isEmpty()) {
            throw new AppComponentsContainerException(String.format("Не смогли найти компонент '%s' в контексте.", componentClass.getCanonicalName()));
        }
        if (components.size() > 1) {
            throw new AppComponentsContainerException(String.format("Несколько кандидатов '%s' найдены в контексте.", componentClass.getCanonicalName()));
        }
        return (C) components.get(0);
    }

    @Override
    public <C> C getAppComponent(String componentName) {
        C component = (C) appComponentsByName.get(componentName);
        if (component == null) {
            throw new AppComponentsContainerException(String.format("Не смогли найти компонент '%s' в контексте.", componentName));
        }
        return component;
    }
}
