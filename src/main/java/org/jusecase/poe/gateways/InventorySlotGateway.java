package org.jusecase.poe.gateways;

import org.jusecase.poe.entities.InventorySlot;

import java.util.function.Consumer;

public interface InventorySlotGateway {
    int COLS = 12;
    int ROWS = 5;

    void getAll(Consumer<InventorySlot> slotConsumer);
}
