package org.jusecase.poe.gateways;

import org.jusecase.inject.Component;
import org.jusecase.poe.entities.InventorySlot;
import org.jusecase.poe.plugins.ImageCapturePlugin;
import org.jusecase.poe.plugins.ImageHashPlugin;

import javax.inject.Inject;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

// TODO how to determine inventory area?

@Component
public class CapturedInventorySlotGateway implements InventorySlotGateway {

    private static final int COLS = 12;
    private static final int ROWS = 5;

    @Inject
    private ImageCapturePlugin imageCapturePlugin;
    @Inject
    private ImageHashPlugin imageHashPlugin;

    private Rectangle inventoryArea;
    private int slotOffsetX;
    private int slotOffsetY;

    @Override
    public List<InventorySlot> getAll() {
        BufferedImage inventoryImage = imageCapturePlugin.captureScreen(inventoryArea);

        int slotWidth = (inventoryArea.width - (slotOffsetX * (COLS - 1))) / COLS;
        int slotHeight = (inventoryArea.height - (slotOffsetY * (ROWS - 1))) / ROWS;
        int slotCenterX = (int) Math.round(0.5 * slotWidth);
        int slotCenterY = (int) Math.round(0.5 * slotHeight);

        int slotX = 0;
        int slotY = 0;

        List<InventorySlot> slots = new ArrayList<>();
        for (int x = 0; x < COLS; ++x) {
            for (int y = 0; y < ROWS; ++y) {
                InventorySlot slot = new InventorySlot();
                slot.imageHash = getHash(inventoryImage, slotX, slotY, slotWidth, slotHeight);
                slot.x = slotX + inventoryArea.x + slotCenterX;
                slot.y = slotY + inventoryArea.y + slotCenterY;
                slots.add(slot);

                slotY += slotHeight + slotOffsetY;
            }
            slotX += slotWidth + slotOffsetX;
            slotY = 0;
        }

        return slots;
    }

    private String getHash(BufferedImage inventoryImage, int slotX, int slotY, int slotWidth, int slotHeight) {
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
