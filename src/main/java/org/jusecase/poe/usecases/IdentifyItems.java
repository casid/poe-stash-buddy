package org.jusecase.poe.usecases;

import org.jusecase.inject.Component;
import org.jusecase.poe.entities.Hash;
import org.jusecase.poe.entities.InventorySlot;
import org.jusecase.poe.entities.Item;
import org.jusecase.poe.entities.ItemType;
import org.jusecase.poe.gateways.InventorySlotGateway;
import org.jusecase.poe.gateways.ItemGateway;
import org.jusecase.poe.gateways.SettingsGateway;
import org.jusecase.poe.plugins.ImageHashPlugin;
import org.jusecase.poe.plugins.InputPlugin;
import org.jusecase.poe.services.ItemTypeService;

import javax.inject.Inject;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class IdentifyItems implements Usecase {

    @Inject
    InventorySlotGateway inventorySlotGateway;
    @Inject
    ItemGateway itemGateway;
    @Inject
    InputPlugin inputPlugin;
    @Inject
    ImageHashPlugin imageHashPlugin;
    @Inject
    ItemTypeService itemTypeService;

    @Override
    public void execute() {
        InventorySlot scrollsOfWisdom = getScrollsOfWisdom();
        if (scrollsOfWisdom == null) {
            return;
        }

        List<InventorySlot> allUnidentified = getAllUnidentified();
        if (allUnidentified.isEmpty()) {
            return;
        }

        inputPlugin.pressShift();
        inputPlugin.rightClick(scrollsOfWisdom.x, scrollsOfWisdom.y);

        for (InventorySlot inventorySlot : allUnidentified) {
            inputPlugin.click(inventorySlot.x, inventorySlot.y);
        }

        inputPlugin.releaseShift();
    }

    private List<InventorySlot> getAllUnidentified() {
        return inventorySlotGateway.getAllUnidentified().stream().filter(s -> itemTypeService.getType(s) != ItemType.MAP).collect(Collectors.toList());
    }

    private InventorySlot getScrollsOfWisdom() {
        Item scrollOfWisdom = itemGateway.getScrollOfWisdom();
        for (InventorySlot inventorySlot : inventorySlotGateway.getIgnored()) {
            for (Hash imageHash : inventorySlot.imageHashes) {
                if (imageHashPlugin.isSimilar(imageHash, scrollOfWisdom.imageHash)) {
                    return inventorySlot;
                }
            }
        }

        return null;
    }
}
