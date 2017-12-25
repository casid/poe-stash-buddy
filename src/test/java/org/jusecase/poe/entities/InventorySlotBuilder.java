package org.jusecase.poe.entities;

import org.jusecase.builders.Builder;

public class InventorySlotBuilder implements Builder<InventorySlot>, InventorySlotBuilderMethods<InventorySlot, InventorySlotBuilder> {
    private InventorySlot entity = new InventorySlot();
    private int distanceToOriginal;

    @Override
    public InventorySlot getEntity() {
        return entity;
    }

    public InventorySlotBuilder chaosOrb() {
        entity.imageHash = TestHash.createHash("chaosOrb", distanceToOriginal);
        return this;
    }

    public InventorySlotBuilder noCurrency() {
        entity.imageHash = TestHash.createHash("noCurrencyWillMatchThisHash", distanceToOriginal);
        return this;
    }

    public InventorySlotBuilder withDistanceToOriginal(int value) {
        this.distanceToOriginal = value;
        return this;
    }

    public static InventorySlotBuilder inventorySlot() {
        return new InventorySlotBuilder();
    }
}