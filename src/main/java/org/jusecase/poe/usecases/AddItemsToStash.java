package org.jusecase.poe.usecases;

import org.jusecase.inject.Component;
import org.jusecase.poe.entities.Item;
import org.jusecase.poe.entities.InventorySlot;
import org.jusecase.poe.entities.ItemType;
import org.jusecase.poe.gateways.ItemGateway;
import org.jusecase.poe.gateways.InventorySlotGateway;
import org.jusecase.poe.plugins.ImageHashPlugin;
import org.jusecase.poe.plugins.InputPlugin;

import javax.inject.Inject;

@Component
public class AddItemsToStash {

    @Inject
    private InputPlugin inputPlugin;
    @Inject
    private InventorySlotGateway inventorySlotGateway;
    @Inject
    private ItemGateway itemGateway;
    @Inject
    private ImageHashPlugin imageHashPlugin;

    public void execute() {
        for (InventorySlot inventorySlot : inventorySlotGateway.getAll()) {
            if (isCurrencySlot(inventorySlot)) {
                inputPlugin.clickWithControlPressed(inventorySlot.x, inventorySlot.y);
            }
        }
    }

    private boolean isCurrencySlot(InventorySlot inventorySlot) {
        for (Item item : itemGateway.getAll()) {
            if (item.type == ItemType.CURRENCY && imageHashPlugin.isSimilar(inventorySlot.imageHash, item.imageHash)) {
                return true;
            }
        }
        return false;
    }
}
