package org.jusecase.poe.gateways;

import org.jusecase.poe.entities.InventorySlot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

public class InventorySlotGatewayTrainer implements InventorySlotGateway {

    private List<InventorySlot> inventorySlots = new ArrayList<>();

    public void givenInventorySlots(InventorySlot... inventorySlots) {
        this.inventorySlots.clear();
        this.inventorySlots.addAll(Arrays.asList(inventorySlots));
    }


    @Override
    public void getAll(Consumer<InventorySlot> slotConsumer) {
        inventorySlots.forEach(slotConsumer);
    }
}