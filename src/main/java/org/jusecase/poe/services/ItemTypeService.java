package org.jusecase.poe.services;

import org.jusecase.inject.Component;
import org.jusecase.poe.entities.Hash;
import org.jusecase.poe.entities.InventorySlot;
import org.jusecase.poe.entities.Item;
import org.jusecase.poe.entities.ItemType;
import org.jusecase.poe.gateways.ItemGateway;
import org.jusecase.poe.plugins.ImageHashPlugin;

import javax.inject.Inject;

@Component
public class ItemTypeService {

    @Inject
    private ItemGateway itemGateway;
    @Inject
    private ImageHashPlugin imageHashPlugin;

    public ItemType getType(InventorySlot inventorySlot) {
        for (Item item : itemGateway.getAll()) {
            for (Hash imageHash : inventorySlot.imageHashes) {
                if (imageHashPlugin.isSimilar(imageHash, item.imageHash)) {
                    return item.type;
                }
            }
        }
        return null;
    }
}
