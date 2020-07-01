package ru.otus.homework;


import com.google.common.base.Joiner;
import com.google.common.collect.ContiguousSet;

public class HelloOtus {

    public static void main(String[] args) {
        System.out.println(Joiner.on(" > ").join(ContiguousSet.closed(100, 110)));
    }
}
