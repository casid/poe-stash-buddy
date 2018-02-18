package org.jusecase.poe.gateways;

import io.github.glytching.junit.extension.folder.TemporaryFolder;
import io.github.glytching.junit.extension.folder.TemporaryFolderExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
    void migration_v0_to_v1() {
        Settings oldSettings = new Settings();
        oldSettings.stashTabLocations.put(ItemType.MAP, new Point(1, 2));
        oldSettings.stashTabLocations.put(ItemType.CURRENCY, new Point(3, 4));
        gateway.saveSettings(oldSettings, 0);

        Settings newSettings = gateway.getSettings();
        assertThat(newSettings.version).isEqualTo(Settings.CURRENT_VERSION);
        assertThat(newSettings.enabledStashTabs).containsExactlyInAnyOrder(ItemType.MAP, ItemType.CURRENCY);
    }
}