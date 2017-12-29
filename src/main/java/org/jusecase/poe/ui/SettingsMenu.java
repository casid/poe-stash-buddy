package org.jusecase.poe.ui;

import layout.SpringUtilities;
import org.jusecase.inject.Component;
import org.jusecase.poe.entities.ItemType;
import org.jusecase.poe.entities.Settings;
import org.jusecase.poe.gateways.SettingsGateway;
import org.jusecase.poe.usecases.SaveSettings;

import javax.inject.Inject;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.EnumMap;

import static org.jusecase.poe.gateways.InventorySlotGateway.COLS;
import static org.jusecase.poe.gateways.InventorySlotGateway.ROWS;

@Component
public class SettingsMenu extends Frame {

    @Inject
    private SettingsGateway settingsGateway;

    private Settings settings;

    private Panel fields;
    private TextField inventoryAreaX;
    private TextField inventoryAreaY;
    private TextField inventoryAreaWidth;
    private TextField inventoryAreaHeight;
    private TextField slotOffsetX;
    private TextField slotOffsetY;
    private EnumMap<ItemType, StashTabLocation> stashTabLocations = new EnumMap<>(ItemType.class);

    public SettingsMenu() throws HeadlessException {
        super("PoE Stash Buddy");

        settings = settingsGateway.getSettings();
        if (settings == null) {
            settings = new Settings();
        } else {
            settings = settings.clone();
        }
    }

    public void init() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        initFields();
        initButtons();

        pack();
    }

    private void initFields() {
        fields = new Panel(new SpringLayout());

        initInventoryArea();
        initSlotOffset();
        initStashTabLocations();
        initIgnoredSlots();

        add(fields);
        SpringUtilities.makeCompactGrid(fields, 3 + stashTabLocations.size(), 2, 10, 10, 10, 10);
    }

    private void initSlotOffset() {
        Label label = new Label("Inventory offset", Label.RIGHT);
        label.setMaximumSize(new Dimension(label.getMinimumSize().width, label.getMinimumSize().height));
        fields.add(label);

        Panel panel = new Panel(new FlowLayout(FlowLayout.LEFT));
        slotOffsetX = new TextField("" + settings.slotOffsetX, 5);
        panel.add(slotOffsetX);
        slotOffsetY = new TextField("" + settings.slotOffsetY, 5);
        panel.add(slotOffsetY);
        panel.setMaximumSize(new Dimension(panel.getMaximumSize().width, panel.getMinimumSize().height));
        fields.add(panel);
    }

    private void initStashTabLocations() {
        for (ItemType itemType : ItemType.values()) {
            Label label = new Label(itemType.getTabName() + " tab", Label.RIGHT);
            label.setMaximumSize(new Dimension(label.getMinimumSize().width, label.getMinimumSize().height));
            fields.add(label);

            StashTabLocation panel = new StashTabLocation(itemType);
            panel.init();
            stashTabLocations.put(itemType, panel);

            panel.setMaximumSize(new Dimension(panel.getMaximumSize().width, panel.getMinimumSize().height));
            fields.add(panel);
        }
    }

    private void initInventoryArea() {
        Label label = new Label("Inventory area", Label.RIGHT);
        label.setMaximumSize(new Dimension(label.getMinimumSize().width, label.getMinimumSize().height));
        fields.add(label);

        Panel panel = new Panel(new FlowLayout(FlowLayout.LEFT));
        inventoryAreaX = new TextField("" + settings.inventoryAreaX, 5);
        panel.add(inventoryAreaX);
        inventoryAreaY = new TextField("" + settings.inventoryAreaY, 5);
        panel.add(inventoryAreaY);
        inventoryAreaWidth = new TextField("" + settings.inventoryAreaWidth, 5);
        panel.add(inventoryAreaWidth);
        inventoryAreaHeight = new TextField("" + settings.inventoryAreaHeight, 5);
        panel.add(inventoryAreaHeight);
        panel.setMaximumSize(new Dimension(panel.getMaximumSize().width, panel.getMinimumSize().height));
        fields.add(panel);
    }

    private void initIgnoredSlots() {
        Label label = new Label("Ignored slots", Label.RIGHT);
        label.setMaximumSize(new Dimension(label.getMinimumSize().width, label.getMinimumSize().height));
        fields.add(label);

        Panel panel = new Panel(new GridLayout(ROWS, COLS, 1, 1));
        for (int y = 0; y < ROWS; ++y) {
            for (int x = 0; x < COLS; ++x) {
                final int slotIndex = x * ROWS + y;

                Panel slot = new Panel();
                slot.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        if (settings.ignoredSlots.contains(slotIndex)) {
                            settings.ignoredSlots.remove(slotIndex);
                        } else {
                            settings.ignoredSlots.add(slotIndex);
                        }
                        updateSlotColor(slot, slotIndex);
                    }
                });

                updateSlotColor(slot, slotIndex);
                panel.add(slot);
            }
        }
        fields.add(panel);
    }

    private void updateSlotColor(Panel slot, int slotIndex) {
        if (settings.ignoredSlots.contains(slotIndex)) {
            slot.setBackground(Color.RED);
        } else {
            slot.setBackground(Color.GREEN);
        }
    }

    private void initButtons() {
        Panel controlPanel = new Panel(new FlowLayout(FlowLayout.LEFT));

        Button save = new Button("Save");
        save.addActionListener(e -> save());
        controlPanel.add(save);

        Button cancel = new Button("Cancel");
        cancel.addActionListener(e -> dispose());
        controlPanel.add(cancel);

        add(controlPanel);
    }

    private void save() {
        settings.inventoryAreaX = parseInt(inventoryAreaX);
        settings.inventoryAreaY = parseInt(inventoryAreaY);
        settings.inventoryAreaWidth = parseInt(inventoryAreaWidth);
        settings.inventoryAreaHeight = parseInt(inventoryAreaHeight);
        settings.slotOffsetX = parseInt(slotOffsetX);
        settings.slotOffsetY = parseInt(slotOffsetY);
        stashTabLocations.values().forEach(StashTabLocation::applyToSettings);
        new SaveSettings().execute(settings);
        dispose();
    }

    private int parseInt(TextField textField) {
        try {
            return Integer.parseInt(textField.getText());
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private class StashTabLocation extends Panel {
        ItemType type;
        Checkbox enabled;
        TextField x;
        TextField y;

        public StashTabLocation(ItemType type) {
            this.type = type;
        }

        void init() {
            setLayout(new FlowLayout(FlowLayout.LEFT));

            enabled = new Checkbox();
            enabled.addItemListener(l -> applyToSettings());
            add(enabled);

            x = new TextField(5);
            add(x);

            y = new TextField(5);
            add(y);

            update();
        }

        void update() {
            Point location = settings.stashTabLocations.get(type);
            if (location == null) {
                enabled.setState(false);
                x.setEnabled(false);
                y.setEnabled(false);
                x.setText("0");
                y.setText("0");
            } else {
                enabled.setState(true);
                x.setEnabled(true);
                y.setEnabled(true);
                x.setText("" + location.x);
                y.setText("" + location.y);
            }
        }

        void applyToSettings() {
            if (enabled.getState()) {
                Point point = settings.stashTabLocations.computeIfAbsent(type, t -> new Point());
                point.x = parseInt(x);
                point.y = parseInt(y);
            } else {
                settings.stashTabLocations.remove(type);
            }
            update();
        }
    }
}
