package org.jusecase.poe.usecases;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.jusecase.inject.ComponentTest;
import org.jusecase.inject.Trainer;
import org.jusecase.poe.entities.Settings;
import org.jusecase.poe.gateways.SettingsGatewayTrainer;

import static org.assertj.core.api.Assertions.assertThat;

class LoadSettingsToEditTest implements ComponentTest {

    @Trainer
    SettingsGatewayTrainer settingsGatewayTrainer;

    Settings loadedSettings;

    @Test
    void noSettingsExist_newSettingsAreCreated() {
        settingsGatewayTrainer.givenSettings(null);
        whenSettingsAreLoaded();
        assertThat(loadedSettings).isNotNull();
    }

    @Test
    void settingsExist_areClonedForEditing() {
        Settings existingSettings = new Settings();
        existingSettings.inputDelayMillis = 1337;
        settingsGatewayTrainer.givenSettings(existingSettings);

        whenSettingsAreLoaded();

        assertThat(loadedSettings).isNotSameAs(existingSettings);
        assertThat(loadedSettings.ignoredSlots).isNotSameAs(existingSettings.ignoredSlots);
        assertThat(loadedSettings.enabledStashTabs).isNotSameAs(existingSettings.enabledStashTabs);
        assertThat(loadedSettings.stashTabLocations).isNotSameAs(existingSettings.stashTabLocations);
        assertThat(loadedSettings.inventoryProfiles).isNotSameAs(existingSettings.inventoryProfiles);
        assertThat(loadedSettings).isEqualToComparingFieldByFieldRecursively(existingSettings);
    }

    private void whenSettingsAreLoaded() {
        loadedSettings = new LoadSettingsToEdit().execute();
    }
}