package org.jusecase.poe.usecases;

import org.jusecase.inject.Component;
import org.jusecase.poe.entities.*;
import org.jusecase.poe.gateways.InventorySlotGateway;
import org.jusecase.poe.gateways.ItemGateway;
import org.jusecase.poe.gateways.SettingsGateway;
import org.jusecase.poe.plugins.ImageHashPlugin;
import org.jusecase.poe.plugins.InputPlugin;
import org.jusecase.poe.services.ItemTypeService;

import javax.inject.Inject;
import java.awt.*;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;

@Component
public class AddItemsToStash implements Usecase {

    @Inject
    private InputPlugin inputPlugin;
    @Inject
    private InventorySlotGateway inventorySlotGateway;
    @Inject
    private SettingsGateway settingsGateway;
    @Inject
    private ItemTypeService itemTypeService;

    @Override
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

        if (!settings.enabledStashTabs.contains(itemType)) {
            return null;
        }

        return settings.stashTabLocations.get(itemType);
    }

    private EnumMap<ItemType, List<InventorySlot>> getSlotsGroupedByType() {
        EnumMap<ItemType, List<InventorySlot>> slotsByType = new EnumMap<>(ItemType.class);
        for (InventorySlot inventorySlot : inventorySlotGateway.getAll()) {
            ItemType type = itemTypeService.getType(inventorySlot);
            if (type != null) {
                slotsByType.computeIfAbsent(type, itemType -> new ArrayList<>()).add(inventorySlot);
            }
        }
        return slotsByType;
    }
}
