package ru.otus.homework.provoker.impl;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Optional;

public final class InvokeUtil {

    private InvokeUtil() {
    }

    public static Optional<Throwable> invokeMethod(Method method, Object instance) {
        if (method != null) {
            try {
                method.invoke(instance);
            } catch (InvocationTargetException ite) {
                return Optional.of(ite.getCause());
            } catch (Throwable t) {
                return Optional.of(t);
            }
        }
        return Optional.empty();
    }
}
