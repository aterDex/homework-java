package ru.otus.homework.hw25.appcontainer;

import org.junit.jupiter.api.Test;
import ru.otus.homework.hw25.appcontainer.api.AppComponentsContainerException;
import ru.otus.homework.hw25.appcontainer.test.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AppComponentsContainerImplTest {

    @Test
    void checkConstructorsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> new AppComponentsContainerImpl((Class<?>) null));
        assertThrows(IllegalArgumentException.class, () -> new AppComponentsContainerImpl(null, null));
        assertThrows(IllegalArgumentException.class, () -> new AppComponentsContainerImpl((Class<?>) null));
        assertThrows(IllegalArgumentException.class, () -> new AppComponentsContainerImpl(NotComponentContainer.class));
        assertThrows(IllegalArgumentException.class, () -> new AppComponentsContainerImpl(ComponentContainerWithoutComponent.class, NotComponentContainer.class));
        assertThrows(IllegalArgumentException.class, () -> new AppComponentsContainerImpl(ComponentContainerWithoutComponent.class, null));
    }

    @Test
    void checkProcessConfig() {
        assertThrows(AppComponentsContainerException.class, () -> new AppComponentsContainerImpl(ComponentContainerNotEmptyConstructor.class));
        assertThrows(AppComponentsContainerException.class, () -> new AppComponentsContainerImpl(ComponentContainerExceptionConstructor.class));
        assertThrows(AppComponentsContainerException.class, () -> new AppComponentsContainerImpl(ComponentContainerWithSameNameComponents.class));
        assertThrows(AppComponentsContainerException.class, () -> new AppComponentsContainerImpl(ComponentContainerWithExceptionComponents.class));
    }

    @Test
    void checkGetAppComponent() {
        var app = new AppComponentsContainerImpl(ComponentContainerWithComponents.class);

        assertThrows(AppComponentsContainerException.class, () -> app.getAppComponent("unknown"));
        assertThrows(IllegalArgumentException.class, () -> app.getAppComponent((String) null));
        assertThrows(IllegalArgumentException.class, () -> app.getAppComponent((Class) null));

        assertNotNull(app.getAppComponent("notComponentContainer"));
        ArrayList<String> component = app.getAppComponent("arrayList");
        assertNotNull(component);

        assertEquals(component, app.getAppComponent(ArrayList.class));
        assertEquals(component, app.getAppComponent(List.class));
        assertEquals(component, app.getAppComponent(Collection.class));
        assertEquals(component, app.getAppComponent(Iterable.class));
        assertEquals(component, app.getAppComponent(RandomAccess.class));
    }

    @Test
    void checkGetAppComponentWithComponentContainerWithSameComponents() {
        var app = new AppComponentsContainerImpl(ComponentContainerWithSameComponents.class);
        assertNotNull(app.getAppComponent(List.class));
        assertNotNull(app.getAppComponent(Map.class));

        assertThrows(AppComponentsContainerException.class, () -> app.getAppComponent(Cloneable.class));
    }

    @Test
    void checkGetAppComponentWithComponentContainerWithStaticComponents() {
        var app = new AppComponentsContainerImpl(ComponentContainerWithStaticComponents.class);
        assertNotNull(app.getAppComponent("notComponentContainer"));
        assertNotNull(app.getAppComponent("arrayList"));
    }
}