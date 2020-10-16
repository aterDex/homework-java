package ru.otus.homework.hw10;

import ru.otus.homework.herald.api.Log;

public final class WorldBuilder {

    private WorldBuilder() {
    }

    @Log(comment = "Good game")
    public static World creatMediocreWorld() {
        World buildingWorld = new World();
        buildingWorld.sayWord("Word");
        buildingWorld._createMatter("sky earth light");
        buildingWorld._createMatter("divide the waters from the waters");
        buildingWorld._createMatter("the dry land appear");
        buildingWorld._createMatter("stars sun moon");
        buildingWorld._createMatter("fish birds");
        buildingWorld._createMatter("people");
        buildingWorld.sayWord("Good");
        return buildingWorld;
    }
}
