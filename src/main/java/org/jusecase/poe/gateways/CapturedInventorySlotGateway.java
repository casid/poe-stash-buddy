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
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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
        return getAll(new ImageHashSlotProcessor(), getIgnoredSlots());
    }

    @Override
    public List<InventorySlot> getAllUnidentified() {
        List<InventorySlot> unidentified = new CopyOnWriteArrayList<>();

        List<InventorySlot> all = getAll(new ImageHashSlotProcessor() {
            @Override
            public void process(InventorySlot slot, BufferedImage inventoryImage, int slotWidth, int slotHeight) {
                super.process(slot, inventoryImage, slotWidth, slotHeight);

                if (isUnidentified(inventoryImage, slot.x, slot.y, slotWidth, slotHeight)) {
                    unidentified.add(slot);
                }
            }
        }, getIgnoredSlots());

        all.removeIf(s -> !unidentified.contains(s));

        return all;
    }

    @Override
    public List<InventorySlot> getIgnored() {
        return getAll(new ImageHashSlotProcessor(), getUnignoredSlots());
    }

    public void setInventoryArea(Rectangle inventoryArea) {
        this.inventoryArea = inventoryArea;
    }

    public void setSlotOffset(int x, int y) {
        slotOffsetX = x;
        slotOffsetY = y;
    }

    private List<InventorySlot> getAll(SlotProcessor slotProcessor, Set<Integer> ignoredSlots) {
        BufferedImage inventoryImage = imageCapturePlugin.captureScreen(inventoryArea);

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
            slotProcessor.process(slot, inventoryImage, (int) slotWidth, (int) slotHeight);
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

    private Set<Integer> getUnignoredSlots() {
        Set<Integer> ignoredSlots = getIgnoredSlots();
        return IntStream.range(0, ROWS * COLS).filter(i -> !ignoredSlots.contains(i)).boxed().collect(Collectors.toSet());
    }

    private Hash getHash(BufferedImage inventoryImage, int slotX, int slotY, int slotWidth, int slotHeight) {
        try {
            slotX = Math.max(0, Math.min(inventoryImage.getWidth() - slotWidth, slotX));
            slotY = Math.max(0, Math.min(inventoryImage.getHeight() - slotHeight, slotY));
            return imageHashPlugin.getHash(inventoryImage.getSubimage(slotX, slotY, slotWidth, slotHeight));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private boolean isUnidentified(BufferedImage inventoryImage, int slotX, int slotY, int slotWidth, int slotHeight) {
        BufferedImage slotImage = inventoryImage.getSubimage(slotX, slotY, slotWidth, slotHeight);
        return getAmountOfUnidentifiedPixels(slotImage) >= 0.075f;
    }

    private float getAmountOfUnidentifiedPixels(BufferedImage slotImage) {
        int unidentifiedPixels = 0;
        int totalPixels = slotImage.getWidth() * slotImage.getHeight();

        for (int x = 0; x < slotImage.getWidth(); ++x) {
            for (int y = 0; y < slotImage.getHeight(); ++y) {
                int rgb = slotImage.getRGB(x, y);

                int r = (rgb >> 16) & 0xff;
                int g = (rgb >> 8) & 0xff;
                int b = rgb & 0xff;

                if (r >= 40 && r <= 45 && g < 5 && b < 5) {
                    ++unidentifiedPixels;
                }
            }
        }

        return (float) unidentifiedPixels / totalPixels;
    }

    private interface SlotProcessor {
        void process(InventorySlot slot, BufferedImage inventoryImage, int slotWidth, int slotHeight);
    }

    private class ImageHashSlotProcessor implements SlotProcessor {

        @Override
        public void process(InventorySlot slot, BufferedImage inventoryImage, int slotWidth, int slotHeight) {
            for (int x = -slotOffsetX; x <= slotOffsetX; ++x) {
                for (int y = -slotOffsetY; y <= slotOffsetY; ++y) {
                    slot.imageHashes.add(getHash(inventoryImage, slot.x + x, slot.y + y, slotWidth, slotHeight));
                }
            }
        }
    }
}
