package org.jusecase.poe.ui;

import org.jusecase.poe.StashBuddy;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.net.URL;

public class TrayMenu {

    private final StashBuddy stashBuddy;

    public TrayMenu(StashBuddy stashBuddy) {
        this.stashBuddy = stashBuddy;
    }

    public void init() {
        try {
            TrayIcon trayIcon = getTrayIcon();

            PopupMenu menu = new PopupMenu();

            MenuItem settings = new MenuItem("PoE Stash Buddy");
            settings.addActionListener(e -> stashBuddy.showSettings());
            menu.add(settings);

            MenuItem exit = new MenuItem("Exit");
            exit.addActionListener(e -> System.exit(0));
            menu.add(exit);

            trayIcon.setPopupMenu(menu);

            SystemTray.getSystemTray().add(trayIcon);
        } catch (AWTException e) {
            throw new RuntimeException(e);
        }
    }

    private TrayIcon getTrayIcon() {
        TrayIcon icon = new TrayIcon(getTrayIconImage(), "PoE Stash Buddy");
        icon.setImageAutoSize(true);
        return icon;
    }

    @SuppressWarnings("ConstantConditions")
    private BufferedImage getTrayIconImage() {
        try {
            URL resource = Thread.currentThread().getContextClassLoader().getResource("icon.png");
            return ImageIO.read(resource);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
