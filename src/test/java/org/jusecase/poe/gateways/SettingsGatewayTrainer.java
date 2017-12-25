package org.jusecase.poe.gateways;

import org.jusecase.poe.entities.Settings;

public class SettingsGatewayTrainer implements SettingsGateway {

    private Settings settings;

    public void givenSettings(Settings settings) {
        this.settings = settings;
    }

    @Override
    public Settings getSettings() {
        return settings;
    }

    @Override
    public void saveSettings(Settings settings) {
    }
}