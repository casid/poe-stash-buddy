package org.jusecase.poe.ui;

import layout.SpringUtilities;
import org.jusecase.inject.Component;
import org.jusecase.poe.entities.Settings;
import org.jusecase.poe.gateways.SettingsGateway;
import org.jusecase.poe.usecases.SaveSettings;

import javax.inject.Inject;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

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
    private TextField inventoryAreaHeight;
    private TextField inventoryAreaWidth;
    private TextField slotOffsetX;
    private TextField slotOffsetY;

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
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent windowEvent) {
                dispose();
            }
        });

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        initFields();
        initButtons();

        pack();
    }

    private void initFields() {
        fields = new Panel(new SpringLayout());

        initInventoryArea();
        initSlotOffset();
        initIgnoredSlots();

        add(fields);
        SpringUtilities.makeCompactGrid(fields, 3, 2, 10, 10, 10, 10);
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

    private void initInventoryArea() {
        Label label = new Label("Inventory area", Label.RIGHT);
        label.setMaximumSize(new Dimension(label.getMinimumSize().width, label.getMinimumSize().height));
        fields.add(label);

        Panel panel = new Panel(new FlowLayout(FlowLayout.LEFT));
        inventoryAreaX = new TextField("" + settings.inventoryAreaX, 5);
        panel.add(inventoryAreaX);
        inventoryAreaY = new TextField("" + settings.inventoryAreaY, 5);
        panel.add(inventoryAreaY);
        inventoryAreaHeight = new TextField("" + settings.inventoryAreaHeight, 5);
        panel.add(inventoryAreaHeight);
        inventoryAreaWidth = new TextField("" + settings.inventoryAreaWidth, 5);
        panel.add(inventoryAreaWidth);
        panel.setMaximumSize(new Dimension(panel.getMaximumSize().width, panel.getMinimumSize().height));
        fields.add(panel);
    }

    private void initIgnoredSlots() {
        Label label = new Label("Ignored slots", Label.RIGHT);
        label.setMaximumSize(new Dimension(label.getMinimumSize().width, label.getMinimumSize().height));
        fields.add(label);

        Panel panel = new Panel(new GridLayout(ROWS, COLS, 1, 1));
        for (int x = 0; x < COLS; ++x) {
            for (int y = 0; y < ROWS; ++y) {
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
        settings.inventoryAreaX = Integer.parseInt(inventoryAreaX.getText());
        settings.inventoryAreaY = Integer.parseInt(inventoryAreaY.getText());
        settings.inventoryAreaWidth = Integer.parseInt(inventoryAreaWidth.getText());
        settings.inventoryAreaHeight = Integer.parseInt(inventoryAreaHeight.getText());
        settings.slotOffsetX = Integer.parseInt(slotOffsetX.getText());
        settings.slotOffsetY = Integer.parseInt(slotOffsetY.getText());
        new SaveSettings().execute(settings);
        dispose();
    }
}
