package org.jusecase.poe.gateways;

import org.jusecase.poe.entities.InventorySlot;

import java.util.function.Consumer;

public interface InventorySlotGateway {
    void getAll(Consumer<InventorySlot> slotConsumer);
}
