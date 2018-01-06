package org.jusecase.poe.usecases;

import org.jusecase.inject.Component;
import org.jusecase.poe.entities.InventorySlot;
import org.jusecase.poe.entities.Item;
import org.jusecase.poe.entities.ItemType;
import org.jusecase.poe.entities.Settings;
import org.jusecase.poe.gateways.InventorySlotGateway;
import org.jusecase.poe.gateways.ItemGateway;
import org.jusecase.poe.gateways.SettingsGateway;
import org.jusecase.poe.plugins.ImageHashPlugin;
import org.jusecase.poe.plugins.InputPlugin;

import javax.inject.Inject;
import java.awt.*;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;

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
    @Inject
    private SettingsGateway settingsGateway;

    public void execute() {
        EnumMap<ItemType, List<InventorySlot>> slotsByType = getSlotsGroupedByType();

        for (ItemType itemType : ItemType.values()) {
            List<InventorySlot> slots = slotsByType.get(itemType);
            if (slots != null) {
                addToStash(itemType, slots);
            }
        }
    }

    private void addToStash(ItemType itemType, List<InventorySlot> slots) {
        Point point = getStashTabLocation(itemType);
        if (point != null) {
            inputPlugin.click(point.x, point.y);
            inputPlugin.waitDefaultTime(itemType.getTabDelayFactor());
            for (InventorySlot slot : slots) {
                inputPlugin.clickWithControlPressed(slot.x, slot.y);
            }
        }
    }

    private Point getStashTabLocation(ItemType itemType) {
        Settings settings = settingsGateway.getSettings();
        if (settings == null) {
            return null;
        }

        return settings.stashTabLocations.get(itemType);
    }

    private EnumMap<ItemType, List<InventorySlot>> getSlotsGroupedByType() {
        EnumMap<ItemType, List<InventorySlot>> slotsByType = new EnumMap<>(ItemType.class);
        for (InventorySlot inventorySlot : inventorySlotGateway.getAll()) {
            ItemType type = getType(inventorySlot);
            if (type != null) {
                slotsByType.computeIfAbsent(type, itemType -> new ArrayList<>()).add(inventorySlot);
            }
        }
        return slotsByType;
    }

    private ItemType getType(InventorySlot inventorySlot) {
        for (Item item : itemGateway.getAll()) {
            if (imageHashPlugin.isSimilar(inventorySlot.imageHash, item.imageHash)) {
                return item.type;
            }
        }
        return null;
    }
}
