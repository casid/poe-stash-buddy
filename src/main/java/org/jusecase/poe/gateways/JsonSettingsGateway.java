package org.jusecase.poe.gateways;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.jusecase.poe.entities.InventoryProfile;
import org.jusecase.poe.entities.Settings;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class JsonSettingsGateway implements SettingsGateway {
    private final Path file;
    private final ObjectMapper mapper = new ObjectMapper();
    private Settings settings;

    @SuppressWarnings("unused") // Called by dependency injection framework
    public JsonSettingsGateway() {
        this(Paths.get(System.getProperty("user.home")).toAbsolutePath().resolve(".poe-stash-buddy").resolve("settings.json"));
    }

    public JsonSettingsGateway(Path file) {
        this.file = file;
    }

    @Override
    public Settings getSettings() {
        if (settings == null) {
            try {
                settings = mapper.readValue(file.toFile(), Settings.class);

                while (settings.version < Settings.CURRENT_VERSION) {
                    migrateSettings();
                }
            } catch (IOException e) {
                settings = null;
            }
        }
        return settings;
    }

    @Override
    public void saveSettings(Settings settings) {
        this.settings = settings;
        saveSettings(settings, Settings.CURRENT_VERSION);
    }

    public void saveSettings(Settings settings, int version) {
        try {
            settings.version = version;
            Files.createDirectories(file.getParent());
            mapper.writeValue(file.toFile(), settings);
        } catch (IOException e) {
            throw new RuntimeException("Failed to store app settings", e);
        }
    }

    private void migrateSettings() {
        if (settings.version == 0) {
            migrateToVersion1();
        } else if (settings.version == 1) {
            migrateToVersion2();
        }
    }

    private void migrateToVersion1() {
        settings.enabledStashTabs.addAll(settings.stashTabLocations.keySet());
        settings.version = 1;
    }

    @SuppressWarnings("deprecation")
    private void migrateToVersion2() {
        InventoryProfile profile = new InventoryProfile();
        profile.inventoryAreaX = settings.inventoryAreaX;
        profile.inventoryAreaY = settings.inventoryAreaY;
        profile.inventoryAreaWidth = settings.inventoryAreaWidth;
        profile.inventoryAreaHeight = settings.inventoryAreaHeight;
        profile.slotOffsetX = settings.slotOffsetX;
        profile.slotOffsetY = settings.slotOffsetY;
        settings.inventoryProfiles.add(profile);
        settings.inventoryProfiles.add(new InventoryProfile());
        settings.activeInventoryProfileIndex = 0;
        settings.version = 2;
    }
}
