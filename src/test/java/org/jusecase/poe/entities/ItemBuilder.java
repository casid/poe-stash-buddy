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

    public ItemBuilder card() {
        return withType(ItemType.CARD).withImageHash(TestHash.createHash("divinationCard"));
    }

    public ItemBuilder map() {
        return withType(ItemType.MAP).withImageHash(TestHash.createHash("Maze of the Minotaur"));
    }

    public ItemBuilder essence() {
        return withType(ItemType.ESSENCE).withImageHash(TestHash.createHash("Essence of Hatred"));
    }

    public ItemBuilder withImageHash(String value) {
        Hash hash = new Hash();
        hash.features = value;
        hash.colors = value;
        entity.imageHash = hash;
        return this;
    }
}