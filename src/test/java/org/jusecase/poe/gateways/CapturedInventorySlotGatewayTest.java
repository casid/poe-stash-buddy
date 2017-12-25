package org.jusecase.poe.gateways;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.jusecase.inject.ComponentTest;
import org.jusecase.inject.Trainer;
import org.jusecase.poe.entities.Currency;
import org.jusecase.poe.entities.InventorySlot;
import org.jusecase.poe.entities.Settings;
import org.jusecase.poe.plugins.ImageCapturePluginTrainer;
import org.jusecase.poe.plugins.ImageHashPlugin;

import java.awt.*;
import java.util.Arrays;
import java.util.List;
import java.util.TreeSet;

import static org.assertj.core.api.Assertions.assertThat;

class CapturedInventorySlotGatewayTest implements ComponentTest {
    @Trainer
    ImageCapturePluginTrainer imageCapturePluginTrainer;
    @Trainer
    SettingsGatewayTrainer settingsGatewayTrainer;

    ImageHashPlugin imageHashPlugin;
    CurrencyGateway currencyGateway;
    CapturedInventorySlotGateway gateway;

    List<InventorySlot> inventorySlots;

    @BeforeEach
    void setUp() {
        givenDependency(imageHashPlugin = new ImageHashPlugin());
        givenDependency(currencyGateway = new ResourceCurrencyGateway());
        gateway = new CapturedInventorySlotGateway();

        imageCapturePluginTrainer.givenImage("inventory-4k-crop-exact.png");
        gateway.setInventoryArea(new Rectangle(2000, 1000, 1260, 524));
        gateway.setSlotOffset(3, 3);
    }

    @Test
    void allSlots_positions() {
        whenGetAllInventorySlots();

        SoftAssertions s = new SoftAssertions();
        s.assertThat(inventorySlots).hasSize(5 * 12);
        s.assertThat(inventorySlots.get(0).x).isEqualTo(2051);
        s.assertThat(inventorySlots.get(0).y).isEqualTo(1051);
        s.assertThat(inventorySlots.get(1).x).isEqualTo(2051);
        s.assertThat(inventorySlots.get(1).y).isEqualTo(1051 + 3 + 102);
        s.assertThat(inventorySlots.get(5).x).isEqualTo(2051 + 3 + 102);
        s.assertThat(inventorySlots.get(5).y).isEqualTo(1051);
        s.assertAll();
    }

    @Test
    void allSlots_ignored() {
        Settings settings = new Settings();
        settings.ignoredSlots = new TreeSet<>(Arrays.asList(0, 1));
        settingsGatewayTrainer.givenSettings(settings);

        whenGetAllInventorySlots();

        SoftAssertions s = new SoftAssertions();
        s.assertThat(inventorySlots).hasSize(5 * 12 - 2);
        s.assertThat(inventorySlots.get(5 - 2).x).isEqualTo(2051 + 3 + 102);
        s.assertThat(inventorySlots.get(5 - 2).y).isEqualTo(1051);
        s.assertAll();
    }

    @Test
    void allSlots_imageHashes() {
        whenGetAllInventorySlots();

        thenSlotContainsCurrency(0, false);
        thenSlotsContainCurrency(1, 4, true);
        thenSlotsContainCurrency(5, 25, false);
        thenSlotsContainCurrency(26, 28, true);
        thenSlotsContainCurrency(29, 32, false);
        thenSlotContainsCurrency(33, true);
        thenSlotsContainCurrency(34, 40, false);
        thenSlotContainsCurrency(41, true);
        thenSlotsContainCurrency(42, 59, false);
    }

    private void thenSlotsContainCurrency(int startIndex, int endIndex, boolean containsCurrency) {
        for (int i = startIndex; i <= endIndex; ++i) {
            thenSlotContainsCurrency(i, containsCurrency);
        }
    }

    private void thenSlotContainsCurrency(int index, boolean containsCurrency) {
        InventorySlot slot = inventorySlots.get(index);
        Currency currency = getMatchingCurrency(slot);

        if (containsCurrency) {
            assertThat(currency).isNotNull();
        } else {
            assertThat(currency).isNull();
        }
    }

    private Currency getMatchingCurrency(InventorySlot slot) {
        for (Currency currency : currencyGateway.getAll()) {
            if (imageHashPlugin.isSimilar(slot.imageHash, currency.imageHash)) {
                return currency;
            }
        }
        return null;
    }

    private void whenGetAllInventorySlots() {
        inventorySlots = gateway.getAll();
    }
}