package org.jusecase.poe.usecases;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.jusecase.inject.ComponentTest;
import org.jusecase.inject.Trainer;
import org.jusecase.poe.entities.Settings;
import org.jusecase.poe.gateways.CapturedInventorySlotGateway;
import org.jusecase.poe.gateways.ResourceItemGateway;
import org.jusecase.poe.gateways.SettingsGatewayTrainer;
import org.jusecase.poe.plugins.ImageCapturePluginTrainer;
import org.jusecase.poe.plugins.ImageHashPlugin;
import org.jusecase.poe.plugins.InputPluginTrainer;
import org.jusecase.poe.services.ItemTypeService;

import java.awt.*;
import java.util.TreeSet;

import static org.jusecase.Builders.a;
import static org.jusecase.Builders.list;

class IdentifyItemsTest implements ComponentTest {

    @Trainer
    ImageCapturePluginTrainer imageCapturePluginTrainer;
    @Trainer
    SettingsGatewayTrainer settingsGatewayTrainer;
    @Trainer
    InputPluginTrainer inputPluginTrainer;

    CapturedInventorySlotGateway capturedInventorySlotGateway;

    IdentifyItems usecase;

    @BeforeEach
    void setUp() {
        givenDependency(new ImageHashPlugin());
        givenDependency(capturedInventorySlotGateway = new CapturedInventorySlotGateway());
        givenDependency(new ResourceItemGateway());
        givenDependency(new ItemTypeService());
        usecase = new IdentifyItems();

        Settings settings = new Settings();
        settings.ignoredSlots = new TreeSet<>(a(list(1, 2, 3, 4)));
        settingsGatewayTrainer.givenSettings(settings);

        capturedInventorySlotGateway.setInventoryArea(new Rectangle(0, 0, 1260, 522));
        capturedInventorySlotGateway.setSlotOffset(3, 3);
    }

    @Test
    void items() {
        imageCapturePluginTrainer.givenImages("inventory-4k-crop-identify-items.png");

        usecase.execute();

        inputPluginTrainer.thenEventsAre(
                "shift pressed",
                "right click(51, 471)", // right click on scroll of wisdom
                "click(156, 51)",
                "click(156, 156)",
                "click(156, 261)",
                "click(262, 51)",
                "click(262, 156)",
                "click(262, 261)",
                "click(367, 51)",
                "click(367, 156)",
                "click(367, 261)",
                "click(367, 366)",
                "click(472, 51)",
                "click(472, 156)",
                "click(472, 261)",
                "click(472, 366)",
                "click(577, 261)",
                "click(577, 366)",
                "click(577, 471)",
                "click(683, 261)",
                "click(683, 471)",
                "click(788, 156)",
                "click(788, 261)",
                "click(788, 366)",
                "click(893, 156)",
                "click(893, 261)",
                "click(893, 366)",
                "click(1209, 261)",
                "click(1209, 366)",
                "click(1209, 471)",
                "shift released" // done, nothing left to identify
        );
    }

    @Test
    void allIdentified() {
        imageCapturePluginTrainer.givenImage("inventory-4k-crop-identify-items-all-identified.png");

        usecase.execute();

        inputPluginTrainer.thenEventsAre();
    }

    @Test
    void mapsAreNotIdentified() {
        imageCapturePluginTrainer.givenImage("inventory-4k-crop-identify-items-map.png");

        usecase.execute();

        inputPluginTrainer.thenEventsAre();
    }

    @Test
    void mapsAreNotIdentified_unlessPlayerSaysSo() {
        imageCapturePluginTrainer.givenImage("inventory-4k-crop-identify-items-map.png");
        settingsGatewayTrainer.getSettings().identifyMaps = true;

        usecase.execute();

        inputPluginTrainer.thenEventsContain("click(156, 51)");
    }
}