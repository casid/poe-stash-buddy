package org.jusecase.poe.gateways;

import org.jusecase.poe.entities.Settings;

public interface SettingsGateway {
    Settings getSettings();
    void saveSettings(Settings settings);
}
