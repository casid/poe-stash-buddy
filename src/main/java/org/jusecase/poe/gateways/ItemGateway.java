package org.jusecase.poe.gateways;

import org.jusecase.poe.entities.Item;

import java.util.List;

public interface ItemGateway {
    List<Item> getAll();
    Item getScrollOfWisdom();
}
