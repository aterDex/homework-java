package ru.otus.homework.hw16;

import com.google.gson.Gson;
import ru.otus.homework.hson.Hson;

public class App {

    public static void main(String[] args) {
        ObjectExample objForTest = ObjectExample.builder()
                .i(10)
                .counter(Long.MAX_VALUE)
                .name("test")
                .objectExampleInternal(null)
                .collection(null)
                .collection(new ObjectExampleInternal(23.03F, 12.43F))
                .collection(new ObjectExampleInternal(44, 23.4234f))
                .build();
        Hson hson = new Hson();
        String json = hson.toJson(objForTest);
        Gson gson = new Gson();
        ObjectExample example = gson.fromJson(json, ObjectExample.class);
        System.out.println(example);
    }
}
