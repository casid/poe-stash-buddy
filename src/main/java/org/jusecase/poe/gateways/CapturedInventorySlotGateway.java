package org.jusecase.poe.gateways;

import org.jusecase.inject.Component;
import org.jusecase.poe.entities.Hash;
import org.jusecase.poe.entities.InventorySlot;
import org.jusecase.poe.entities.Settings;
import org.jusecase.poe.plugins.ImageCapturePlugin;
import org.jusecase.poe.plugins.ImageHashPlugin;

import javax.inject.Inject;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

@Component
public class CapturedInventorySlotGateway implements InventorySlotGateway {

    @Inject
    private ImageCapturePlugin imageCapturePlugin;
    @Inject
    private ImageHashPlugin imageHashPlugin;
    @Inject
    private SettingsGateway settingsGateway;

    private Rectangle inventoryArea;
    private int slotOffsetX;
    private int slotOffsetY;

    @Override
    public List<InventorySlot> getAll() {
        BufferedImage inventoryImage = imageCapturePlugin.captureScreen(inventoryArea);

        Set<Integer> ignoredSlots = getIgnoredSlots();

        double slotWidth = ((double) inventoryArea.width - (slotOffsetX * (COLS - 1))) / COLS;
        double slotHeight = ((double) inventoryArea.height - (slotOffsetY * (ROWS - 1))) / ROWS;
        int slotCenterX = (int) Math.round(0.5 * slotWidth);
        int slotCenterY = (int) Math.round(0.5 * slotHeight);

        double slotX = 0;
        double slotY = 0;

        List<InventorySlot> slots = new ArrayList<>();
        for (int x = 0; x < COLS; ++x) {
            for (int y = 0; y < ROWS; ++y) {
                if (!ignoredSlots.contains(x * ROWS + y)) {
                    InventorySlot slot = new InventorySlot();
                    slot.x = (int) Math.round(slotX);
                    slot.y = (int) Math.round(slotY);
                    slots.add(slot);
                }

                slotY += slotHeight + slotOffsetY;
            }
            slotX += slotWidth + slotOffsetX;
            slotY = 0;
        }

        slots.parallelStream().forEach(slot -> {
            slot.imageHash = getHash(inventoryImage, slot.x, slot.y, (int) slotWidth, (int) slotHeight);
            slot.x += inventoryArea.x + slotCenterX;
            slot.y += inventoryArea.y + slotCenterY;
        });

        return slots;
    }

    private Set<Integer> getIgnoredSlots() {
        Settings settings = settingsGateway.getSettings();
        if (settings == null) {
            return Collections.emptySet();
        }

        return settings.ignoredSlots;
    }

    private Hash getHash(BufferedImage inventoryImage, int slotX, int slotY, int slotWidth, int slotHeight) {
        try {
            return imageHashPlugin.getHash(inventoryImage.getSubimage(slotX, slotY, slotWidth, slotHeight));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void setInventoryArea(Rectangle inventoryArea) {
        this.inventoryArea = inventoryArea;
    }

    public void setSlotOffset(int x, int y) {
        slotOffsetX = x;
        slotOffsetY = y;
    }
}
