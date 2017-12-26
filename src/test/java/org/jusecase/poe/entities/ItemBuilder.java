package org.jusecase.poe.entities;

import org.jusecase.builders.Builder;

public class ItemBuilder implements Builder<Item>, ItemBuilderMethods<Item, ItemBuilder> {

    private Item entity = new Item();

    @Override
    public Item getEntity() {
        return entity;
    }

    public ItemBuilder chaosOrb() {
        return withType(ItemType.CURRENCY).withImageHash(TestHash.createHash("chaosOrb"));
    }

    public ItemBuilder exaltedOrb() {
        return withType(ItemType.CURRENCY).withImageHash(TestHash.createHash("exaltedOrb"));
    }

    public static ItemBuilder item() {
        return new ItemBuilder();
    }
}