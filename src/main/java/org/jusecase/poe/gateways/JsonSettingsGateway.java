package org.jusecase.poe.gateways;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.jusecase.poe.entities.Settings;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class JsonSettingsGateway implements SettingsGateway {
    private final Path file;
    private final ObjectMapper mapper = new ObjectMapper();
    private Settings settings;

    public JsonSettingsGateway() {
        Path settingsDirectory = Paths.get(System.getProperty("user.home")).toAbsolutePath();
        file = settingsDirectory.resolve(".poe-stash-buddy").resolve("settings.json");
    }

    @Override
    public Settings getSettings() {
        if (settings == null) {
            try {
                settings = mapper.readValue(file.toFile(), Settings.class);
            } catch (IOException e) {
                settings = null;
            }
        }
        return settings;
    }

    @Override
    public void saveSettings(Settings settings) {
        try {
            this.settings = settings;
            Files.createDirectories(file.getParent());
            mapper.writeValue(file.toFile(), settings);
        } catch (IOException e) {
            throw new RuntimeException("Failed to store app settings", e);
        }
    }
}
