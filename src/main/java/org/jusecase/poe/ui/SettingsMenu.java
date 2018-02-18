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
public class SettingsMenu extends JFrame {

    @Inject
    private SettingsGateway settingsGateway;

    private Settings settings;

    private JPanel fields;
    private JTextField inventoryAreaX;
    private JTextField inventoryAreaY;
    private JTextField inventoryAreaWidth;
    private JTextField inventoryAreaHeight;
    private JTextField slotOffsetX;
    private JTextField slotOffsetY;
    private JTextField inputDelayMillis;
    private JCheckBox identifyMaps;
    private EnumMap<ItemType, StashTabLocation> stashTabLocations = new EnumMap<>(ItemType.class);

    public SettingsMenu() {
        super("PoE Stash Buddy");

        settings = settingsGateway.getSettings();
        if (settings == null) {
            settings = new Settings();
        } else {
            settings = settings.clone();
        }
    }

    public void init() {
        setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));

        initFields();
        initButtons();

        pack();
    }

    private void initFields() {
        fields = new JPanel(new SpringLayout());

        initHotkey();
        initInventoryArea();
        initSlotOffset();
        initStashTabLocations();
        initIgnoredSlots();
        initInputDelay();
        initIdentifyMaps();

        add(fields);
        SpringUtilities.makeCompactGrid(fields, 3 + stashTabLocations.size() + 1 + 1 + 1, 2, 10, 10, 10, 10);
    }

    private void initHotkey() {
        JLabel label = new JLabel("Hotkeys", JLabel.RIGHT);
        label.setMaximumSize(new Dimension(label.getMinimumSize().width, label.getMinimumSize().height));
        fields.add(label);

        JLabel hotkey = new JLabel("<html>Add currency to stash: <b>Ctrl + Shift + A</b><br/>Identify items: <b>Ctrl + Shift + V</b></html>", JLabel.LEFT);
        hotkey.setMaximumSize(new Dimension(hotkey.getMinimumSize().width, 4 * hotkey.getMinimumSize().height));
        fields.add(hotkey);
    }

    private void initSlotOffset() {
        JLabel label = new JLabel("Inventory offset", JLabel.RIGHT);
        label.setMaximumSize(new Dimension(label.getMinimumSize().width, label.getMinimumSize().height));
        fields.add(label);

        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        slotOffsetX = new JTextField("" + settings.slotOffsetX, 5);
        panel.add(slotOffsetX);
        slotOffsetY = new JTextField("" + settings.slotOffsetY, 5);
        panel.add(slotOffsetY);
        panel.setMaximumSize(new Dimension(panel.getMaximumSize().width, panel.getMinimumSize().height));
        fields.add(panel);
    }

    private void initStashTabLocations() {
        for (ItemType itemType : ItemType.values()) {
            JLabel label = new JLabel(itemType.getTabName() + " tab", JLabel.RIGHT);
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
        JLabel label = new JLabel("Inventory area", JLabel.RIGHT);
        label.setMaximumSize(new Dimension(label.getMinimumSize().width, label.getMinimumSize().height));
        fields.add(label);

        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        inventoryAreaX = new JTextField("" + settings.inventoryAreaX, 5);
        panel.add(inventoryAreaX);
        inventoryAreaY = new JTextField("" + settings.inventoryAreaY, 5);
        panel.add(inventoryAreaY);
        inventoryAreaWidth = new JTextField("" + settings.inventoryAreaWidth, 5);
        panel.add(inventoryAreaWidth);
        inventoryAreaHeight = new JTextField("" + settings.inventoryAreaHeight, 5);
        panel.add(inventoryAreaHeight);
        panel.setMaximumSize(new Dimension(panel.getMaximumSize().width, panel.getMinimumSize().height));
        fields.add(panel);
    }

    private void initIgnoredSlots() {
        JLabel label = new JLabel("Ignored slots", JLabel.RIGHT);
        label.setMaximumSize(new Dimension(label.getMinimumSize().width, label.getMinimumSize().height));
        fields.add(label);

        JPanel panel = new JPanel(new GridLayout(ROWS, COLS, 1, 1));
        for (int y = 0; y < ROWS; ++y) {
            for (int x = 0; x < COLS; ++x) {
                final int slotIndex = x * ROWS + y;

                JPanel slot = new JPanel();
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

    private void initInputDelay() {
        JLabel label = new JLabel("Input delay (ms)", JLabel.RIGHT);
        label.setMaximumSize(new Dimension(label.getMinimumSize().width, label.getMinimumSize().height));
        fields.add(label);

        inputDelayMillis = new JTextField("" + settings.inputDelayMillis, 5);
        fields.add(inputDelayMillis);
    }

    private void initIdentifyMaps() {
        JLabel label = new JLabel("Identify maps", JLabel.RIGHT);
        label.setMaximumSize(new Dimension(label.getMinimumSize().width, label.getMinimumSize().height));
        fields.add(label);

        identifyMaps = new JCheckBox(null, null, settings.identifyMaps);
        fields.add(identifyMaps);
    }

    private void updateSlotColor(JPanel slot, int slotIndex) {
        if (settings.ignoredSlots.contains(slotIndex)) {
            slot.setBackground(Color.RED);
        } else {
            slot.setBackground(Color.GREEN);
        }
    }

    private void initButtons() {
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        JButton apply = new JButton("Apply");
        apply.addActionListener(e -> apply());
        controlPanel.add(apply);

        JButton save = new JButton("Save");
        save.addActionListener(e -> save());
        controlPanel.add(save);

        JButton cancel = new JButton("Cancel");
        cancel.addActionListener(e -> close());
        controlPanel.add(cancel);

        add(controlPanel);
    }

    private void close() {
        dispose();
    }

    private void apply() {
        settings.inventoryAreaX = parseInt(inventoryAreaX);
        settings.inventoryAreaY = parseInt(inventoryAreaY);
        settings.inventoryAreaWidth = parseInt(inventoryAreaWidth);
        settings.inventoryAreaHeight = parseInt(inventoryAreaHeight);
        settings.slotOffsetX = parseInt(slotOffsetX);
        settings.slotOffsetY = parseInt(slotOffsetY);
        stashTabLocations.values().forEach(StashTabLocation::applyToSettings);
        settings.inputDelayMillis = parseInt(inputDelayMillis);
        settings.identifyMaps = identifyMaps.isSelected();
        new SaveSettings().execute(settings);
    }

    private void save() {
        apply();
        close();
    }

    private int parseInt(JTextField textField) {
        try {
            return Integer.parseInt(textField.getText());
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private class StashTabLocation extends JPanel {
        ItemType type;
        JCheckBox enabled;
        JTextField x;
        JTextField y;

        public StashTabLocation(ItemType type) {
            this.type = type;
        }

        void init() {
            setLayout(new FlowLayout(FlowLayout.LEFT));

            enabled = new JCheckBox();
            enabled.addActionListener(l -> applyToSettings());
            add(enabled);

            x = new JTextField(5);
            add(x);

            y = new JTextField(5);
            add(y);

            update();
        }

        void update() {
            boolean tabEnabled = settings.enabledStashTabs.contains(type);
            enabled.setSelected(tabEnabled);
            x.setEnabled(tabEnabled);
            y.setEnabled(tabEnabled);

            Point location = settings.stashTabLocations.get(type);
            if (location == null) {
                x.setText("0");
                y.setText("0");
            } else {
                x.setText("" + location.x);
                y.setText("" + location.y);
            }
        }

        void applyToSettings() {
            if (enabled.isSelected()) {
                settings.enabledStashTabs.add(type);
            } else {
                settings.enabledStashTabs.remove(type);
            }

            Point point = settings.stashTabLocations.computeIfAbsent(type, t -> new Point());
            point.x = parseInt(x);
            point.y = parseInt(y);

            update();
        }
    }
}
