package org.jusecase.poe.gateways;

import io.github.glytching.junit.extension.folder.TemporaryFolder;
import io.github.glytching.junit.extension.folder.TemporaryFolderExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.jusecase.poe.entities.InventoryProfile;
import org.jusecase.poe.entities.ItemType;
import org.jusecase.poe.entities.Settings;

import java.awt.*;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(TemporaryFolderExtension.class)
class JsonSettingsGatewayTest {
    JsonSettingsGateway gateway;

    @BeforeEach
    void setUp(TemporaryFolder temporaryFolder) {
        gateway = new JsonSettingsGateway(temporaryFolder.createDirectory("tmp").toPath().resolve("settings.json"));
    }

    @Test
    void noSettingsExist() {
        Settings settings = gateway.getSettings();
        assertThat(settings).isNull();
    }

    @Test
    void save() {
        Settings expected = new Settings();
        expected.activeInventoryProfileIndex = 1;
        expected.inventoryProfiles.add(new InventoryProfile());
        expected.inventoryProfiles.add(new InventoryProfile());

        gateway.saveSettings(expected, Settings.CURRENT_VERSION);
        Settings actual = gateway.getSettings();

        assertThat(actual).isEqualToComparingFieldByFieldRecursively(expected);
    }

    @Test
    void migration_v0_to_v1() {
        Settings oldSettings = new Settings();
        oldSettings.stashTabLocations.put(ItemType.MAP, new Point(1, 2));
        oldSettings.stashTabLocations.put(ItemType.CURRENCY, new Point(3, 4));
        gateway.saveSettings(oldSettings, 0);

        Settings newSettings = gateway.getSettings();
        assertThat(newSettings.version).isEqualTo(Settings.CURRENT_VERSION);
        assertThat(newSettings.enabledStashTabs).containsExactlyInAnyOrder(ItemType.MAP, ItemType.CURRENCY);
    }

    @SuppressWarnings("deprecation")
    @Test
    void migration_v1_to_v2() {
        Settings oldSettings = new Settings();
        oldSettings.inventoryAreaX = 1;
        oldSettings.inventoryAreaY = 2;
        oldSettings.inventoryAreaWidth = 3;
        oldSettings.inventoryAreaHeight = 4;
        oldSettings.slotOffsetX = 5;
        oldSettings.slotOffsetY = 6;
        gateway.saveSettings(oldSettings, 1);

        Settings newSettings = gateway.getSettings();
        assertThat(newSettings.version).isEqualTo(Settings.CURRENT_VERSION);
        assertThat(newSettings.activeInventoryProfileIndex).isEqualTo(0);
        assertThat(newSettings.inventoryProfiles).hasSize(2);
        assertThat(newSettings.inventoryProfiles.get(0).inventoryAreaX).isEqualTo(oldSettings.inventoryAreaX);
        assertThat(newSettings.inventoryProfiles.get(0).inventoryAreaY).isEqualTo(oldSettings.inventoryAreaY);
        assertThat(newSettings.inventoryProfiles.get(0).inventoryAreaWidth).isEqualTo(oldSettings.inventoryAreaWidth);
        assertThat(newSettings.inventoryProfiles.get(0).inventoryAreaHeight).isEqualTo(oldSettings.inventoryAreaHeight);
        assertThat(newSettings.inventoryProfiles.get(0).slotOffsetX).isEqualTo(oldSettings.slotOffsetX);
        assertThat(newSettings.inventoryProfiles.get(0).slotOffsetY).isEqualTo(oldSettings.slotOffsetY);
        assertThat(newSettings.inventoryProfiles.get(1)).isNotNull();
    }
}