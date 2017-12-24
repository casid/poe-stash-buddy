package org.jusecase.poe.entities;

import org.jusecase.builders.Builder;

@javax.annotation.Generated(value="jusecase-builders-generator")
public interface InventorySlotBuilderMethods<T extends InventorySlot, B extends Builder> extends Builder<T> {
    @Override
    default T build() {
        return getEntity();
    }

    T getEntity();

    default B withImageHash(String value) {
        getEntity().imageHash = value;
        return (B)this;
    }

    default B withX(int value) {
        getEntity().x = value;
        return (B)this;
    }

    default B withY(int value) {
        getEntity().y = value;
        return (B)this;
    }
}
