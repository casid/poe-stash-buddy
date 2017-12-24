package org.jusecase.poe.usecases;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.jusecase.inject.ComponentTest;
import org.jusecase.inject.Trainer;
import org.jusecase.poe.gateways.CurrencyGatewayTrainer;
import org.jusecase.poe.gateways.InventorySlotGatewayTrainer;
import org.jusecase.poe.plugins.ImageHashPlugin;
import org.jusecase.poe.plugins.InputPluginTrainer;

import static org.jusecase.Builders.a;
import static org.jusecase.poe.entities.CurrencyBuilder.currency;
import static org.jusecase.poe.entities.InventorySlotBuilder.inventorySlot;

class AddCurrencyToStashTest implements ComponentTest {
    @Trainer
    InventorySlotGatewayTrainer inventorySlotGatewayTrainer;
    @Trainer
    InputPluginTrainer inputPluginTrainer;
    @Trainer
    CurrencyGatewayTrainer currencyGatewayTrainer;

    AddCurrencyToStash usecase;

    @BeforeEach
    void setUp() {
        givenDependency(new ImageHashPlugin());
        currencyGatewayTrainer.givenCurrency(a(currency().chaosOrb()));
        currencyGatewayTrainer.givenCurrency(a(currency().exaltedOrb()));
        usecase = new AddCurrencyToStash();
    }

    @Test
    void noSlotsDetected() {
        inventorySlotGatewayTrainer.givenInventorySlots();
        whenCurrencyIsAddedToStash();
        inputPluginTrainer.thenNoClickWithControlPressed();
    }

    @Test
    void oneSlotDetected_perfectMatch() {
        inventorySlotGatewayTrainer.givenInventorySlots(a(inventorySlot().chaosOrb().withX(200).withY(100)));
        whenCurrencyIsAddedToStash();
        inputPluginTrainer.thenClickedWithControlPressedAt(200, 100);
    }

    @Test
    void oneSlotDetected_perfectMatch2() {
        inventorySlotGatewayTrainer.givenInventorySlots(a(inventorySlot().chaosOrb().withX(400).withY(500)));
        whenCurrencyIsAddedToStash();
        inputPluginTrainer.thenClickedWithControlPressedAt(400, 500);
    }

    @Test
    void oneSlotDetected_closeMatch() {
        inventorySlotGatewayTrainer.givenInventorySlots(a(inventorySlot().withDistanceToOriginal(5).chaosOrb()));
        whenCurrencyIsAddedToStash();
        inputPluginTrainer.thenClickedWithControlPressedCountIs(1);
    }

    @Test
    void onlyClickOncePerSlot() {
        currencyGatewayTrainer.givenCurrency(a(currency().chaosOrb()));
        inventorySlotGatewayTrainer.givenInventorySlots(a(inventorySlot().chaosOrb()));
        whenCurrencyIsAddedToStash();
        inputPluginTrainer.thenClickedWithControlPressedCountIs(1);
    }

    @Test
    void oneSlotDetected_noCurrency() {
        inventorySlotGatewayTrainer.givenInventorySlots(a(inventorySlot().noCurrency()));
        whenCurrencyIsAddedToStash();
        inputPluginTrainer.thenNoClickWithControlPressed();
    }

    private void whenCurrencyIsAddedToStash() {
        usecase.execute();
    }
}