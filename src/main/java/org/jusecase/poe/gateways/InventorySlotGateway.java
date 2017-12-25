package org.jusecase.poe.gateways;

import org.jusecase.poe.entities.InventorySlot;

import java.util.List;

public interface InventorySlotGateway {
    int COLS = 12;
    int ROWS = 5;

    List<InventorySlot> getAll();
}
