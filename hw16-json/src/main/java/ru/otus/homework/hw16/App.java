package ru.otus.homework.hw16;

import com.google.gson.Gson;
import ru.otus.homework.hson.Hson;

public class App {

    public static void main(String[] args) {
        var originalObject = ObjectExample.builder()
                .i(10)
                .counter(Long.MAX_VALUE)
                .name("test")
                .objectExampleInternal(null)
                .collection(null)
                .collection(new ObjectExampleInternal(23.03F, 12.43F))
                .collection(new ObjectExampleInternal(44, 23.4234f))
                .uk("Object as text")
                .mass(new long[][]{{10, 20, 30}, {-10, -20}, {0}})
                .build();

        String json = new Hson().toJson(originalObject);

        System.out.println("Serializer result: " + json);

        var repairObject = new Gson().fromJson(json, ObjectExample.class);


        compare(originalObject, repairObject);
        System.out.println("Change counter in deserialize object");
        repairObject.setCounter(100L);
        compare(originalObject, repairObject);
    }

    private static void compare(ObjectExample originalObject, ObjectExample repairObject) {
        System.out.println("Original and deserialize object is " + (repairObject.equals(originalObject) ? "EQUALS" : "NOT equals"));
    }
}
