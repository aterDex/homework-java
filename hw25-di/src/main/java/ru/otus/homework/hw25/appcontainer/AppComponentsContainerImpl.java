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
        Arrays.stream(initialConfigClasses)
                .forEach(this::checkConfigClass);
        Arrays.stream(initialConfigClasses)
                .sorted(Comparator.comparingInt(x -> x.getAnnotation(AppComponentsContainerConfig.class).order()))
                .forEachOrdered(this::processConfig);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <C> C getAppComponent(Class<C> componentClass) {
        if (componentClass == null) {
            throw new IllegalArgumentException("componentClass не может быть null.");
        }
        log.debug("getAppComponent by class {}", componentClass.getCanonicalName());
        List<Object> components = appComponents.stream()
                .filter(x -> componentClass.isInstance(x))
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
    @SuppressWarnings("unchecked")
    public <C> C getAppComponent(String componentName) {
        if (componentName == null) {
            throw new IllegalArgumentException("componentName не может быть null.");
        }
        log.debug("getAppComponent by name {}", componentName);
        C component = (C) appComponentsByName.get(componentName);
        if (component == null) {
            throw new AppComponentsContainerException(String.format("Не смогли найти компонент '%s' в контексте.", componentName));
        }
        return component;
    }

    private void processConfig(Class<?> configClass) {
        try {
            log.debug("processConfig {}", configClass.getCanonicalName());
            final Object instance = initInstanceForConfig(configClass);
            Arrays.stream(configClass.getDeclaredMethods())
                    .filter(x -> x.isAnnotationPresent(AppComponent.class))
                    .sorted(Comparator.comparingInt(x -> x.getAnnotation(AppComponent.class).order()))
                    .forEachOrdered(x -> processComponentMethod(x, instance));
        } catch (Exception e) {
            clearContext();
            throw e;
        }
    }

    private void clearContext() {
        appComponents.clear();
        appComponentsByName.clear();
    }

    private Object initInstanceForConfig(Class<?> configClass) {
        try {
            var constructor = configClass.getConstructor();
            constructor.setAccessible(true);
            return constructor.newInstance();
        } catch (Exception e) {
            throw new AppComponentsContainerException(String.format("Не смогли создать экземпляр класса '%s'.", configClass.getCanonicalName()), e);
        }
    }

    private void processComponentMethod(Method method, Object instance) {
        var componentMeta = method.getAnnotation(AppComponent.class);
        if (appComponentsByName.containsKey(componentMeta.name())) {
            throw new AppComponentsContainerException(String.format("Компонент с именем '%s' уже находится в контексте. В контексте не может быть компонентов с одинаковыми именами.", componentMeta.name()));
        }
        try {
            method.setAccessible(true);
            var instanceComponent = method.invoke(instance, resolveComponentArgument(method));
            appComponents.add(instanceComponent);
            appComponentsByName.put(componentMeta.name(), instanceComponent);
        } catch (Exception e) {
            throw new AppComponentsContainerException(String.format("Не смогли создать компонент '%s'.", componentMeta.name()), e);
        }
    }

    private Object[] resolveComponentArgument(Method method) {
        if (method.getParameterCount() == 0) {
            return null;
        }
        var args = new Object[method.getParameterCount()];
        var parameters = method.getParameters();
        for (int i = 0; i < args.length; i++) {
            args[i] = getAppComponent(parameters[i].getType());
        }
        return args;
    }

    private void checkConfigClass(Class<?> configClass) {
        if (configClass == null) {
            throw new IllegalArgumentException("configClass не может быть null.");
        }
        if (!configClass.isAnnotationPresent(AppComponentsContainerConfig.class)) {
            throw new IllegalArgumentException(String.format("Переданный класс не является конфигурацией '%s'", configClass.getName()));
        }
    }
}
