package org.jusecase.poe.ui;

import layout.SpringUtilities;
import org.jusecase.inject.Component;
import org.jusecase.poe.entities.Settings;
import org.jusecase.poe.gateways.SettingsGateway;
import org.jusecase.poe.usecases.SaveSettings;

import javax.inject.Inject;
import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

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

        add(fields);
        SpringUtilities.makeCompactGrid(fields, 2, 2, 10, 10, 10, 10);
    }

    private void initSlotOffset() {
        Label label2 = new Label("Inventory offset", Label.RIGHT);
        label2.setMaximumSize(new Dimension(label2.getMinimumSize().width, label2.getMinimumSize().height));
        fields.add(label2);

        Panel panel2 = new Panel(new FlowLayout(FlowLayout.LEFT));
        slotOffsetX = new TextField("" + settings.slotOffsetX, 5);
        panel2.add(slotOffsetX);
        slotOffsetY = new TextField("" + settings.slotOffsetY, 5);
        panel2.add(slotOffsetY);
        panel2.setMaximumSize(new Dimension(panel2.getMaximumSize().width, panel2.getMinimumSize().height));
        fields.add(panel2);
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
