package org.jusecase.poe.ui;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class Settings extends Frame {
    public Settings() throws HeadlessException {
        super("PoE Stash Buddy");
    }

    public void init() {
        setSize(400, 400);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent windowEvent) {
                dispose();
            }
        });

        Panel controlPanel = new Panel();
        controlPanel.setLayout(new FlowLayout());

        Button save = new Button("Save");
        save.addActionListener(e -> dispose());
        controlPanel.add(save);

        Button cancel = new Button("Cancel");
        cancel.addActionListener(e -> dispose());
        controlPanel.add(cancel);

        add(controlPanel);
    }
}
