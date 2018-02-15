package org.jusecase.poe.entities;

import java.nio.file.Path;

public class Item {
    public ItemType type;
    public Path path;
    public String image;
    public Hash imageHash;
    public boolean unidentified;

    @Override
    public String toString() {
        return "Item{" +
                "type=" + type +
                ", path=" + path +
                ", image='" + image + '\'' +
                ", imageHash=" + imageHash +
                ", unidentified=" + unidentified +
                '}';
    }
}
