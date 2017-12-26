package org.jusecase.poe.entities;

import org.jusecase.builders.Builder;

public class CurrencyBuilder implements Builder<Item>, CurrencyBuilderMethods<Item, CurrencyBuilder> {

    private Item entity = new Item();

    @Override
    public Item getEntity() {
        return entity;
    }

    public CurrencyBuilder chaosOrb() {
        entity.imageHash = TestHash.createHash("chaosOrb");
        return this;
    }

    public CurrencyBuilder exaltedOrb() {
        entity.imageHash = TestHash.createHash("exaltedOrb");
        return this;
    }

    public static CurrencyBuilder currency() {
        return new CurrencyBuilder();
    }
}