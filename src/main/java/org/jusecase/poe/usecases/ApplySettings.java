package org.jusecase.poe.usecases;

import org.jusecase.inject.Component;
import org.jusecase.poe.entities.Settings;
import org.jusecase.poe.gateways.CapturedInventorySlotGateway;
import org.jusecase.poe.gateways.SettingsGateway;

import javax.inject.Inject;
import java.awt.*;

@Component
public class ApplySettings {

    @Inject
    private SettingsGateway settingsGateway;
    @Inject
    private CapturedInventorySlotGateway capturedInventorySlotGateway;

    public void execute() {
        Settings settings = settingsGateway.getSettings();
        capturedInventorySlotGateway.setInventoryArea(new Rectangle(
                settings.inventoryAreaX,
                settings.inventoryAreaY,
                settings.inventoryAreaWidth,
                settings.inventoryAreaHeight
        ));
        capturedInventorySlotGateway.setSlotOffset(settings.slotOffsetX, settings.slotOffsetY);
    }
}
