package ru.otus.homework.hw25.appcontainer.test;

import ru.otus.homework.hw25.appcontainer.api.AppComponent;
import ru.otus.homework.hw25.appcontainer.api.AppComponentsContainerConfig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@AppComponentsContainerConfig(order = 0)
public class ComponentContainerWithSameComponents {

    @AppComponent(order = 0, name = "arrayList")
    private List<String> component0() {
        return new ArrayList<>();
    }

    @AppComponent(order = 0, name = "hashMap")
    private Map<String, String> component1() {
        return new HashMap<>();
    }
}
