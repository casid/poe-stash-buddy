package org.jusecase.poe.gateways;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.jusecase.inject.ComponentTest;
import org.jusecase.inject.Trainer;
import org.jusecase.poe.entities.InventoryProfile;
import org.jusecase.poe.entities.Settings;

import static org.assertj.core.api.Assertions.assertThat;

class SettingsInventoryProfileGatewayTest implements ComponentTest {
    InventoryProfileGateway inventoryProfileGateway;

    @Trainer
    SettingsGatewayTrainer settingsGatewayTrainer;

    Settings settings;
    InventoryProfile activeProfile;

    @BeforeEach
    void setUp() {
        inventoryProfileGateway = new SettingsInventoryProfileGateway();

        settings = new Settings();
        settingsGatewayTrainer.givenSettings(settings);
    }

    @Test
    void nullSettings() {
        settingsGatewayTrainer.givenSettings(null);
        whenGetActiveProfile();
        assertThat(activeProfile).isNotNull();
    }

    @Test
    void noProfiles() {
        settings.inventoryProfiles.clear();
        whenGetActiveProfile();
        assertThat(activeProfile).isNotNull();
    }

    @Test
    void oneProfile() {
        settings.inventoryProfiles.add(new InventoryProfile());
        whenGetActiveProfile();
        assertThat(activeProfile).isSameAs(settings.inventoryProfiles.get(0));
    }

    @Test
    void twoProfiles() {
        settings.inventoryProfiles.add(new InventoryProfile());
        settings.inventoryProfiles.add(new InventoryProfile());
        settings.activeInventoryProfileIndex = 1;
        whenGetActiveProfile();
        assertThat(activeProfile).isSameAs(settings.inventoryProfiles.get(1));
    }

    private void whenGetActiveProfile() {
        activeProfile = inventoryProfileGateway.getActiveInventoryProfile();
    }
}