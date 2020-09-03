package ru.otus.homework.hw16;

import com.google.gson.Gson;
import ru.otus.homework.hson.Hson;
import ru.otus.homework.hson.adpter.StringBuilderAdapter;

public class App {

    public static void main(String[] args) {
        var objForTest = ObjectExample.builder()
                .i(10)
                .counter(Long.MAX_VALUE)
                .name("test")
                .objectExampleInternal(null)
                .collection(null)
                .collection(new ObjectExampleInternal(23.03F, 12.43F))
                .collection(new ObjectExampleInternal(44, 23.4234f))
                .uk("Object as text")
                .build();

        String json = new Hson().toJson(objForTest);

        System.out.println("Serializer result: " + json);

        var example = new Gson().fromJson(json, ObjectExample.class);

        System.out.printf("Original and deserialize object is " + (example.equals(objForTest) ? "EQUALS" : "NOT equals"));
    }
}
