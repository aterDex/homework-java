package ru.otus.homework.hw25.appcontainer.api;

public interface AppComponentsContainer {
    <C> C getAppComponent(Class<C> componentClass);
    <C> C getAppComponent(String componentName);
}
