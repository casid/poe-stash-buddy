package org.jusecase.poe.gateways;


import org.jusecase.poe.entities.Item;

import java.util.ArrayList;
import java.util.List;

public class ItemGatewayTrainer implements ItemGateway {
    private List<Item> items = new ArrayList<>();

    @Override
    public List<Item> getAll() {
        return items;
    }

    public void givenCurrency(Item item) {
        this.items.add(item);
    }
}