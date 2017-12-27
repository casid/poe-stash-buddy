package org.jusecase.poe.entities;

import java.nio.file.Path;

public class Item {
    public ItemType type;
    public Path path;
    public String image;
    public Hash imageHash;

    @Override
    public String toString() {
        return "Item{" +
                "type=" + type +
                ", image='" + image + '\'' +
                ", imageHash='" + imageHash + '\'' +
                '}';
    }
}
