package org.jusecase.poe;

import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;
import org.jusecase.inject.Component;
import org.jusecase.inject.Injector;
import org.jusecase.poe.gateways.*;
import org.jusecase.poe.plugins.ImageHashPlugin;
import org.jusecase.poe.plugins.NativeHookPlugin;
import org.jusecase.poe.plugins.RobotPlugin;
import org.jusecase.poe.ui.SettingsMenu;
import org.jusecase.poe.ui.TrayMenu;
import org.jusecase.poe.usecases.AddItemsToStash;
import org.jusecase.poe.usecases.ApplySettings;

import javax.inject.Inject;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import static org.jnativehook.NativeInputEvent.CTRL_MASK;
import static org.jnativehook.NativeInputEvent.SHIFT_MASK;

@Component
public class StashBuddy implements Runnable, NativeKeyListener {

    private SettingsMenu settingsMenu;

    public static void main(String[] args) throws Exception {
        Injector injector = Injector.getInstance();
        injector.add(JsonSettingsGateway.class);
        injector.add(ImageHashPlugin.class);
        injector.add(ResourceItemGateway.class);
        injector.add(RobotPlugin.class);
        injector.add(NativeHookPlugin.class);
        injector.add(CapturedInventorySlotGateway.class);

        new StashBuddy().run();
    }

    @Inject
    private NativeHookPlugin nativeHookPlugin;
    @Inject
    private SettingsGateway settingsGateway;
    @Inject
    private ItemGateway itemGateway;

    @Override
    public void run() {
        initUi();
        initHooks();
        initItems();
    }

    private void initItems() {
        itemGateway.getAll();
    }

    private void initHooks() {
        nativeHookPlugin.enable();
        Runtime.getRuntime().addShutdownHook(new Thread(nativeHookPlugin::disable));

        nativeHookPlugin.addListener(this);
    }

    private void initUi() {
        TrayMenu menu = new TrayMenu(this);
        menu.init();

        if (settingsGateway.getSettings() == null) {
            showSettings();
        } else {
            new ApplySettings().execute();
        }
    }

    public void showSettings() {
        if (settingsMenu == null) {
            settingsMenu = new SettingsMenu();
            settingsMenu.init();
            settingsMenu.addWindowListener(new WindowAdapter() {
                public void windowClosing(WindowEvent windowEvent) {
                    settingsMenu.dispose();
                    settingsMenu = null;
                }
            });
            settingsMenu.setVisible(true);
        } else {
            settingsMenu.requestFocus();
        }
    }

    @Override
    public void nativeKeyTyped(NativeKeyEvent nativeKeyEvent) {
    }

    @Override
    public void nativeKeyPressed(NativeKeyEvent nativeKeyEvent) {
        int modifiers = nativeKeyEvent.getModifiers();
        if ((modifiers & CTRL_MASK) != 0 && (modifiers & SHIFT_MASK) != 0 && nativeKeyEvent.getKeyCode() == NativeKeyEvent.VC_A) {
            new AddItemsToStash().execute();
        }
    }

    @Override
    public void nativeKeyReleased(NativeKeyEvent nativeKeyEvent) {
    }
}
