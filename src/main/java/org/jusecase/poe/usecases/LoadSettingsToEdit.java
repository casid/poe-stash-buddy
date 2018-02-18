package org.jusecase.poe.usecases;

import org.jusecase.inject.Component;
import org.jusecase.poe.entities.Settings;
import org.jusecase.poe.gateways.SettingsGateway;

import javax.inject.Inject;

@Component
public class LoadSettingsToEdit {

    @Inject
    private SettingsGateway settingsGateway;

    public Settings execute() {
        Settings settings = settingsGateway.getSettings();
        if (settings == null) {
            settings = new Settings();
        } else {
            settings = settings.clone();
        }

        return settings;
    }
}
