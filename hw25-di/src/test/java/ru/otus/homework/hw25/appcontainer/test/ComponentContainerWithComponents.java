package ru.otus.homework.hw25.appcontainer.test;

import ru.otus.homework.hw25.appcontainer.api.AppComponent;
import ru.otus.homework.hw25.appcontainer.api.AppComponentsContainerConfig;

import java.util.ArrayList;
import java.util.List;

@AppComponentsContainerConfig(order = 0)
public class ComponentContainerWithComponents {

    @AppComponent(order = 0, name = "arrayList")
    private List<String> component0() {
        return new ArrayList<>();
    }

    @AppComponent(order = 0, name = "notComponentContainer")
    private NotComponentContainer component1() {
        return new NotComponentContainer();
    }
}
