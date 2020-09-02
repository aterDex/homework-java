package ru.otus.homework.hw16;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ObjectExampleInternal {

    private float lat;
    private float lon;

    public ObjectExampleInternal(float lat, float lon) {
        this.lat = lat;
        this.lon = lon;
    }

    public ObjectExampleInternal() {
    }
}
