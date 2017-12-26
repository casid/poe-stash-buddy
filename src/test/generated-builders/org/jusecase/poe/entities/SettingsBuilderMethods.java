package org.jusecase.poe.entities;

import org.jusecase.builders.Builder;

@javax.annotation.Generated(value="jusecase-builders-generator")
public interface SettingsBuilderMethods<T extends Settings, B extends Builder> extends Builder<T> {
    @Override
    default T build() {
        return getEntity();
    }

    T getEntity();

    default B withIgnoredSlots(java.util.SortedSet value) {
        getEntity().ignoredSlots = value;
        return (B)this;
    }

    default B withInventoryAreaHeight(int value) {
        getEntity().inventoryAreaHeight = value;
        return (B)this;
    }

    default B withInventoryAreaWidth(int value) {
        getEntity().inventoryAreaWidth = value;
        return (B)this;
    }

    default B withInventoryAreaX(int value) {
        getEntity().inventoryAreaX = value;
        return (B)this;
    }

    default B withInventoryAreaY(int value) {
        getEntity().inventoryAreaY = value;
        return (B)this;
    }

    default B withSlotOffsetX(int value) {
        getEntity().slotOffsetX = value;
        return (B)this;
    }

    default B withSlotOffsetY(int value) {
        getEntity().slotOffsetY = value;
        return (B)this;
    }
}
