package org.jusecase.poe.gateways;

import org.jusecase.inject.Component;
import org.jusecase.poe.entities.InventoryProfile;
import org.jusecase.poe.entities.Settings;

import javax.inject.Inject;

@Component
public class SettingsInventoryProfileGateway implements InventoryProfileGateway {
    @Inject
    private SettingsGateway settingsGateway;

    @Override
    public InventoryProfile getActiveInventoryProfile() {
        Settings settings = settingsGateway.getSettings();
        if (settings == null) {
            return new InventoryProfile();
        }

        InventoryProfile profile = settings.getActiveInventoryProfile();
        if (profile == null) {
            return new InventoryProfile();
        }

        return profile;
    }
}
