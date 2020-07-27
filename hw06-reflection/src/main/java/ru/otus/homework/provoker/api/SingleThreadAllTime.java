package ru.otus.homework.provoker.api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

// Если класс теста помечен этим атрибутом, он весь должен гарантированно выполнится в рамках одного потока.
@Retention(value = RetentionPolicy.RUNTIME)
@Target(value = {ElementType.TYPE})
public @interface SingleThreadAllTime {
}
