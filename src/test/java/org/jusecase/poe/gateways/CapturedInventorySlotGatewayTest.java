package org.jusecase.poe.gateways;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.jusecase.inject.ComponentTest;
import org.jusecase.inject.Trainer;
import org.jusecase.poe.entities.InventorySlot;
import org.jusecase.poe.entities.Item;
import org.jusecase.poe.entities.Settings;
import org.jusecase.poe.plugins.ImageCapturePluginTrainer;
import org.jusecase.poe.plugins.ImageHashPlugin;
import org.jusecase.poe.services.ItemTypeService;

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
    ItemGateway itemGateway;
    CapturedInventorySlotGateway gateway;
    ItemTypeService itemTypeService;

    List<InventorySlot> inventorySlots;

    @BeforeEach
    void setUp() {
        givenDependency(imageHashPlugin = new ImageHashPlugin());
        givenDependency(itemGateway = new ResourceItemGateway());
        gateway = new CapturedInventorySlotGateway();
        itemTypeService = new ItemTypeService();

        imageCapturePluginTrainer.givenImage("inventory-4k-crop-exact.png");
        gateway.setInventoryArea(new Rectangle(2000, 1000, 1260, 522));
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
        s.assertThat(inventorySlots.get(0).x).isEqualTo(2051);
        s.assertThat(inventorySlots.get(0).y).isNotEqualTo(1051);
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

    @Test
    void ditherSamplesToCheckMoreThanOneHash() {
        imageCapturePluginTrainer.givenImage("inventory-4k-crop-exact-sextant.png");
        gateway.setInventoryArea(new Rectangle(2000, 1000, 1260, 524));

        whenGetAllInventorySlots();

        thenSlotContainsCurrency(29, true);
    }

    @Test
    void essence() {
        imageCapturePluginTrainer.givenImage("inventory-4k-crop-exact-essence.png");
        gateway.setInventoryArea(new Rectangle(2000, 1000, 1260, 524));
        //itemTypeService.setDebug(true);

        whenGetAllInventorySlots();

        thenSlotContainsCurrency(0, true);
        thenSlotContainsCurrency(13, true);
        thenSlotContainsCurrency(14, true);
        thenSlotContainsCurrency(19, true);
    }

    @Test
    void essence_corruption() {
        imageCapturePluginTrainer.givenImage("inventory-4k-crop-exact-essence-corruption.png");
        gateway.setInventoryArea(new Rectangle(2000, 1000, 1260, 524));
        //itemTypeService.setDebug(true);

        whenGetAllInventorySlots();

        thenSlotContainsCurrency(0, true);
    }

    @Test
    void smallerResolution() {
        imageCapturePluginTrainer.givenImage("inventory-2k-crop-exact.png");
        gateway.setInventoryArea(new Rectangle(0, 0, 631, 261));
        gateway.setSlotOffset(1, 1);

        whenGetAllInventorySlots();

        thenSlotsContainCurrency(0, 0, false);
        thenSlotsContainCurrency(1, 1, true);
        thenSlotsContainCurrency(2, 4, false);
        thenSlotsContainCurrency(5, 7, true);
        thenSlotsContainCurrency(8, 9, false);
        thenSlotsContainCurrency(10, 12, true);
        thenSlotsContainCurrency(13, 15, true);
        thenSlotsContainCurrency(16, 44, false);
        thenSlotsContainCurrency(45, 45, true);
    }

    private void thenSlotsContainCurrency(int startIndex, int endIndex, boolean containsCurrency) {
        for (int i = startIndex; i <= endIndex; ++i) {
            thenSlotContainsCurrency(i, containsCurrency);
        }
    }

    private void thenSlotContainsCurrency(int index, boolean containsCurrency) {
        InventorySlot slot = inventorySlots.get(index);
        Item item = getMatchingCurrency(slot);

        if (containsCurrency) {
            assertThat(item).describedAs("expecting item at slot " + index + " to contain currency").isNotNull();
        } else {
            assertThat(item).describedAs("expecting item at slot " + index + " to contain no currency").isNull();
        }
    }

    private Item getMatchingCurrency(InventorySlot slot) {
        return itemTypeService.getMatchingItem(slot);
    }

    private void whenGetAllInventorySlots() {
        inventorySlots = gateway.getAll();
    }
}