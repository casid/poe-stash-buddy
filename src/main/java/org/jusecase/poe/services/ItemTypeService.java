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

    private boolean debug;

    public ItemType getType(InventorySlot inventorySlot) {
        Item item = getMatchingItem(inventorySlot);
        if (item != null) {
            return item.type;
        }
        return null;
    }

    public Item getMatchingItem(InventorySlot inventorySlot) {
        for (Item item : itemGateway.getAll()) {
            if (debug) {
                System.out.println("Testing item " + item);
            }

            for (Hash imageHash : inventorySlot.imageHashes) {
                if (debug) {
                    System.out.println(imageHashPlugin.describeDistance(imageHash, item.imageHash));
                }

                if (imageHashPlugin.isSimilar(imageHash, item.imageHash)) {
                    return item;
                }
            }
        }
        return null;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }
}
