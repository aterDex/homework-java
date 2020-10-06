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
        var appWithComponent = new AppComponentsContainerImpl(ComponentContainerWithComponents.class);

        assertThrows(AppComponentsContainerException.class, () -> appWithComponent.getAppComponent("unknown"));
        assertThrows(IllegalArgumentException.class, () -> appWithComponent.getAppComponent((String) null));
        assertThrows(IllegalArgumentException.class, () -> appWithComponent.getAppComponent((Class) null));

        assertNotNull(appWithComponent.getAppComponent("notComponentContainer"));
        ArrayList<String> component = appWithComponent.getAppComponent("arrayList");
        assertNotNull(component);

        assertEquals(component, appWithComponent.getAppComponent(ArrayList.class));
        assertEquals(component, appWithComponent.getAppComponent(List.class));
        assertEquals(component, appWithComponent.getAppComponent(Collection.class));
        assertEquals(component, appWithComponent.getAppComponent(Iterable.class));
        assertEquals(component, appWithComponent.getAppComponent(RandomAccess.class));

        var appWithSameComponent = new AppComponentsContainerImpl(ComponentContainerWithSameComponents.class);
        assertNotNull(appWithSameComponent.getAppComponent(List.class));
        assertNotNull(appWithSameComponent.getAppComponent(Map.class));

        assertThrows(AppComponentsContainerException.class, () -> appWithSameComponent.getAppComponent(Cloneable.class));
    }
}