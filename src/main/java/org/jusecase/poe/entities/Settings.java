package org.jusecase.poe.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.awt.*;
import java.util.*;
import java.util.List;

@SuppressWarnings("DeprecatedIsStillUsed")
public class Settings implements Cloneable {
    public static final int CURRENT_VERSION = 2;

    @Deprecated // use inventory profiles instead, needs to stay for migration of old json files
    public int inventoryAreaX;
    @Deprecated // use inventory profiles instead, needs to stay for migration of old json files
    public int inventoryAreaY;
    @Deprecated // use inventory profiles instead, needs to stay for migration of old json files
    public int inventoryAreaWidth;
    @Deprecated // use inventory profiles instead, needs to stay for migration of old json files
    public int inventoryAreaHeight;
    @Deprecated // use inventory profiles instead, needs to stay for migration of old json files
    public int slotOffsetX;
    @Deprecated // use inventory profiles instead, needs to stay for migration of old json files
    public int slotOffsetY;

    public List<InventoryProfile> inventoryProfiles = new ArrayList<>(2);
    public int activeInventoryProfileIndex;
    public int inputDelayMillis = 30;
    public SortedSet<Integer> ignoredSlots = new TreeSet<>();
    public EnumSet<ItemType> enabledStashTabs = EnumSet.noneOf(ItemType.class);
    public EnumMap<ItemType, Point> stashTabLocations = new EnumMap<>(ItemType.class);
    public boolean identifyMaps;
    public int version;

    @Override
    public Settings clone() {
        try {
            Settings clone = (Settings) super.clone();
            clone.ignoredSlots = new TreeSet<>(ignoredSlots);
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

    @JsonIgnore
    public InventoryProfile getActiveInventoryProfile() {
        if (activeInventoryProfileIndex >= inventoryProfiles.size()) {
            return null;
        }

        return inventoryProfiles.get(activeInventoryProfileIndex);
    }
}
