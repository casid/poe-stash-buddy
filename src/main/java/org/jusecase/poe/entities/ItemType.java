package org.jusecase.poe.entities;

public enum ItemType {
    CURRENCY, CARD, ESSENCE, MAP;

    public String getTabName() {
        switch (this) {
            case CURRENCY:
                return "Currency";
            case CARD:
                return "Divination card";
            case ESSENCE:
                return "Essence";
            case MAP:
                return "Map";
        }
        return null;
    }
}
