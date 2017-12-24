package org.jusecase.poe.gateways;

import org.jusecase.poe.entities.InventorySlot;

import java.util.List;

public interface InventorySlotGateway {
    List<InventorySlot> getAll();
}
