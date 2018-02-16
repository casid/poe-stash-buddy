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
        return withImageHash(TestHash.createHash("chaosOrb", distanceToOriginal));
    }

    public InventorySlotBuilder noCurrency() {
        return withImageHash(TestHash.createHash("noCurrencyWillMatchThisHash", distanceToOriginal));
    }

    public InventorySlotBuilder withDistanceToOriginal(int value) {
        this.distanceToOriginal = value;
        return this;
    }

    public InventorySlotBuilder card() {
        return withImageHash(TestHash.createHash("divinationCard", distanceToOriginal));
    }

    public static InventorySlotBuilder inventorySlot() {
        return new InventorySlotBuilder();
    }

    public InventorySlotBuilder map() {
        return withImageHash(TestHash.createHash("Maze of the Minotaur", distanceToOriginal));
    }

    public InventorySlotBuilder essence() {
        return withImageHash(TestHash.createHash("Essence of Hatred", distanceToOriginal));
    }

    public InventorySlotBuilder withImageHash(String value) {
        Hash hash = new Hash();
        hash.features = value;
        hash.colors1 = value;
        hash.colors2 = value;
        entity.imageHashes.add(hash);
        return this;
    }
}