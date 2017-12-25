package org.jusecase.poe;

import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;
import org.jusecase.inject.Component;
import org.jusecase.inject.Injector;
import org.jusecase.poe.gateways.CapturedInventorySlotGateway;
import org.jusecase.poe.gateways.JsonSettingsGateway;
import org.jusecase.poe.gateways.ResourceCurrencyGateway;
import org.jusecase.poe.gateways.SettingsGateway;
import org.jusecase.poe.plugins.ImageHashPlugin;
import org.jusecase.poe.plugins.NativeHookPlugin;
import org.jusecase.poe.plugins.RobotPlugin;
import org.jusecase.poe.ui.SettingsMenu;
import org.jusecase.poe.ui.TrayMenu;
import org.jusecase.poe.usecases.AddCurrencyToStash;
import org.jusecase.poe.usecases.ApplySettings;

import javax.inject.Inject;

import static org.jnativehook.NativeInputEvent.CTRL_MASK;
import static org.jnativehook.NativeInputEvent.SHIFT_MASK;

@Component
public class StashBuddy implements Runnable, NativeKeyListener {

    public static void main(String[] args) throws Exception {
        Injector injector = Injector.getInstance();
        injector.add(JsonSettingsGateway.class);
        injector.add(ImageHashPlugin.class);
        injector.add(ResourceCurrencyGateway.class);
        injector.add(RobotPlugin.class);
        injector.add(NativeHookPlugin.class);
        injector.add(CapturedInventorySlotGateway.class);

        new StashBuddy().run();
    }

    @Inject
    private NativeHookPlugin nativeHookPlugin;
    @Inject
    private SettingsGateway settingsGateway;

    @Override
    public void run() {
        initUi();
        initHooks();
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
        SettingsMenu settingsMenu = new SettingsMenu();
        settingsMenu.init();
        settingsMenu.setVisible(true);
    }

    @Override
    public void nativeKeyTyped(NativeKeyEvent nativeKeyEvent) {
    }

    @Override
    public void nativeKeyPressed(NativeKeyEvent nativeKeyEvent) {
        int modifiers = nativeKeyEvent.getModifiers();
        if ((modifiers & CTRL_MASK) != 0 && (modifiers & SHIFT_MASK) != 0 && nativeKeyEvent.getKeyCode() == 46) {
            new AddCurrencyToStash().execute();
        }
    }

    @Override
    public void nativeKeyReleased(NativeKeyEvent nativeKeyEvent) {
    }
}
