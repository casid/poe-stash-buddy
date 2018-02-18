package org.jusecase.poe.usecases;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.jusecase.inject.ComponentTest;
import org.jusecase.inject.Trainer;
import org.jusecase.poe.entities.ItemType;
import org.jusecase.poe.entities.Settings;
import org.jusecase.poe.gateways.ItemGatewayTrainer;
import org.jusecase.poe.gateways.InventorySlotGatewayTrainer;
import org.jusecase.poe.gateways.SettingsGatewayTrainer;
import org.jusecase.poe.plugins.ImageHashPlugin;
import org.jusecase.poe.plugins.InputPluginTrainer;
import org.jusecase.poe.services.ItemTypeService;

import java.awt.*;

import static org.jusecase.Builders.a;
import static org.jusecase.poe.entities.ItemBuilder.item;
import static org.jusecase.poe.entities.InventorySlotBuilder.inventorySlot;

class AddItemsToStashTest implements ComponentTest {
    @Trainer
    InventorySlotGatewayTrainer inventorySlotGatewayTrainer;
    @Trainer
    InputPluginTrainer inputPluginTrainer;
    @Trainer
    ItemGatewayTrainer itemGatewayTrainer;
    @Trainer
    SettingsGatewayTrainer settingsGatewayTrainer;

    Settings settings = new Settings();

    AddItemsToStash usecase;

    @BeforeEach
    void setUp() {
        givenDependency(new ImageHashPlugin());
        givenDependency(new ItemTypeService());

        itemGatewayTrainer.givenItem(a(item().chaosOrb()));
        itemGatewayTrainer.givenItem(a(item().exaltedOrb()));
        itemGatewayTrainer.givenItem(a(item().card()));
        itemGatewayTrainer.givenItem(a(item().map()));
        itemGatewayTrainer.givenItem(a(item().essence()));

        settings.stashTabLocations.put(ItemType.CURRENCY, new Point(1, 2));
        settings.stashTabLocations.put(ItemType.CARD, new Point(3, 4));
        settings.stashTabLocations.put(ItemType.ESSENCE, new Point(5, 6));
        settings.stashTabLocations.put(ItemType.MAP, new Point(7, 8));
        settings.enabledStashTabs.addAll(settings.stashTabLocations.keySet());
        settingsGatewayTrainer.givenSettings(settings);

        usecase = new AddItemsToStash();
    }

    @Test
    void noSlotsDetected() {
        inventorySlotGatewayTrainer.givenInventorySlots();
        whenItemsAreAddedToStash();
        inputPluginTrainer.thenNoClickWithControlPressed();
    }

    @Test
    void oneSlotDetected_perfectMatch() {
        inventorySlotGatewayTrainer.givenInventorySlots(a(inventorySlot().chaosOrb().withX(200).withY(100)));
        whenItemsAreAddedToStash();
        inputPluginTrainer.thenClickedWithControlPressedAt(200, 100);
    }

    @Test
    void oneSlotDetected_perfectMatch2() {
        inventorySlotGatewayTrainer.givenInventorySlots(a(inventorySlot().chaosOrb().withX(400).withY(500)));
        whenItemsAreAddedToStash();
        inputPluginTrainer.thenClickedWithControlPressedAt(400, 500);
    }

    @Test
    void oneSlotDetected_closeMatch() {
        inventorySlotGatewayTrainer.givenInventorySlots(a(inventorySlot().withDistanceToOriginal(5).chaosOrb()));
        whenItemsAreAddedToStash();
        inputPluginTrainer.thenClickedWithControlPressedCountIs(1);
    }

    @Test
    void onlyClickOncePerSlot() {
        itemGatewayTrainer.givenItem(a(item().chaosOrb()));
        inventorySlotGatewayTrainer.givenInventorySlots(a(inventorySlot().chaosOrb()));
        whenItemsAreAddedToStash();
        inputPluginTrainer.thenClickedWithControlPressedCountIs(1);
    }

    @Test
    void oneSlotDetected_noCurrency() {
        inventorySlotGatewayTrainer.givenInventorySlots(a(inventorySlot().noCurrency()));
        whenItemsAreAddedToStash();
        inputPluginTrainer.thenNoClickWithControlPressed();
    }

    @Test
    void oneSlotDetected_card() {
        inventorySlotGatewayTrainer.givenInventorySlots(a(inventorySlot().card().withX(200).withY(100)));
        whenItemsAreAddedToStash();
        inputPluginTrainer.thenClickedWithControlPressedAt(200, 100);
    }

    @Test
    void towSlotsDetected_cardAndCurrency() {
        inventorySlotGatewayTrainer.givenInventorySlots(
                a(inventorySlot().card().withX(1).withY(1)),
                a(inventorySlot().card().withX(2).withY(2)),
                a(inventorySlot().chaosOrb().withX(3).withY(3))
        );

        whenItemsAreAddedToStash();

        inputPluginTrainer.thenClicked(0, settings.stashTabLocations.get(ItemType.CURRENCY));
        inputPluginTrainer.thenClickedWithControlPressedAt(0, 3, 3);
        inputPluginTrainer.thenClicked(1, settings.stashTabLocations.get(ItemType.CARD));
        inputPluginTrainer.thenClickedWithControlPressedAt(1, 1, 1);
        inputPluginTrainer.thenClickedWithControlPressedAt(2, 2, 2);
    }

    @Test
    void twoSlotsDetected_cardAndCurrency_noStashTabLocationForCards() {
        inventorySlotGatewayTrainer.givenInventorySlots(
                a(inventorySlot().card().withX(1).withY(1)),
                a(inventorySlot().chaosOrb().withX(2).withY(2))
        );
        settings.enabledStashTabs.remove(ItemType.CARD);

        whenItemsAreAddedToStash();

        inputPluginTrainer.thenEventsAre(
                "click(1, 2)",
                "ctrl click(2, 2)"
        );
    }

    @Test
    void nullSettings() {
        settingsGatewayTrainer.givenSettings(null);
        inventorySlotGatewayTrainer.givenInventorySlots(a(inventorySlot().card().withX(200).withY(100)));

        whenItemsAreAddedToStash();

        inputPluginTrainer.thenNoClickWithControlPressed();
    }

    @Test
    void towSlotsDetected_allTypes() {
        inventorySlotGatewayTrainer.givenInventorySlots(
                a(inventorySlot().card().withX(1).withY(1)),
                a(inventorySlot().map().withX(2).withY(2)),
                a(inventorySlot().chaosOrb().withX(3).withY(3)),
                a(inventorySlot().essence().withX(4).withY(4))
        );

        whenItemsAreAddedToStash();

        inputPluginTrainer.thenClicked(0, settings.stashTabLocations.get(ItemType.CURRENCY));
        inputPluginTrainer.thenClickedWithControlPressedAt(0, 3, 3);
        inputPluginTrainer.thenClicked(1, settings.stashTabLocations.get(ItemType.CARD));
        inputPluginTrainer.thenClickedWithControlPressedAt(1, 1, 1);
        inputPluginTrainer.thenClicked(2, settings.stashTabLocations.get(ItemType.ESSENCE));
        inputPluginTrainer.thenClickedWithControlPressedAt(2, 4, 4);
        inputPluginTrainer.thenClicked(3, settings.stashTabLocations.get(ItemType.MAP));
        inputPluginTrainer.thenClickedWithControlPressedAt(3, 2, 2);
    }

    private void whenItemsAreAddedToStash() {
        usecase.execute();
    }
}