package org.jusecase.poe.usecases;

import org.jusecase.inject.Component;
import org.jusecase.poe.entities.InventoryProfile;
import org.jusecase.poe.gateways.CapturedInventorySlotGateway;
import org.jusecase.poe.gateways.InventoryProfileGateway;

import javax.inject.Inject;
import java.awt.*;

@Component
public class ApplySettings {

    @Inject
    private InventoryProfileGateway inventoryProfileGateway;
    @Inject
    private CapturedInventorySlotGateway capturedInventorySlotGateway;

    public void execute() {
        InventoryProfile profile = inventoryProfileGateway.getActiveInventoryProfile();
        capturedInventorySlotGateway.setInventoryArea(new Rectangle(profile.inventoryAreaX, profile.inventoryAreaY, profile.inventoryAreaWidth, profile.inventoryAreaHeight));
        capturedInventorySlotGateway.setSlotOffset(profile.slotOffsetX, profile.slotOffsetY);
    }
}
