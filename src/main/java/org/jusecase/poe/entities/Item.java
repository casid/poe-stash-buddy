package org.jusecase.poe.entities;

public class Item {
    public ItemType type;
    public String image;
    public String imageHash;

    @Override
    public String toString() {
        return "Item{" +
                "type=" + type +
                ", image='" + image + '\'' +
                ", imageHash='" + imageHash + '\'' +
                '}';
    }
}
