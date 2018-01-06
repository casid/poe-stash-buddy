package org.jusecase.poe.entities;

import org.jusecase.poe.plugins.InputPlugin;

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

    public int getTabDelayFactor() {
        switch (this) {
            case MAP:
                return 10;
            default:
                return 1;
        }
    }
}
