package org.jusecase.poe;

import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;
import org.jusecase.inject.Component;
import org.jusecase.inject.Injector;
import org.jusecase.poe.gateways.*;
import org.jusecase.poe.plugins.ImageHashPlugin;
import org.jusecase.poe.plugins.NativeHookPlugin;
import org.jusecase.poe.plugins.RobotImageCapturePlugin;
import org.jusecase.poe.services.ItemTypeService;
import org.jusecase.poe.ui.SettingsMenu;
import org.jusecase.poe.ui.TrayMenu;
import org.jusecase.poe.usecases.AddItemsToStash;
import org.jusecase.poe.usecases.ApplySettings;
import org.jusecase.poe.usecases.IdentifyItems;
import org.jusecase.poe.usecases.Usecase;
import com.bulenkov.darcula.DarculaLaf;

import javax.inject.Inject;
import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import static org.jnativehook.NativeInputEvent.CTRL_MASK;
import static org.jnativehook.NativeInputEvent.SHIFT_MASK;

@Component
public class StashBuddy implements Runnable, NativeKeyListener {

    public static void main(String[] args) throws Exception {
        Thread.setDefaultUncaughtExceptionHandler((t, e) -> {
            String message = e.getClass().getSimpleName() + " (" + e.getMessage() + ")";
            if (e.getCause() != null) {
                message += "\nCaused by " + e.getCause().getClass().getSimpleName() + " (" + e.getMessage() + ")";
            }
            e.printStackTrace();
            JOptionPane.showConfirmDialog(null, message, "PoE Stash Buddy - Error", JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        });

        UIManager.setLookAndFeel(new DarculaLaf());

        Injector injector = Injector.getInstance();
        injector.add(JsonSettingsGateway.class);
        injector.add(ImageHashPlugin.class);
        injector.add(ResourceItemGateway.class);
        injector.add(RobotImageCapturePlugin.class);
        injector.add(NativeHookPlugin.class);
        injector.add(CapturedInventorySlotGateway.class);
        injector.add(ItemTypeService.class);

        new StashBuddy().run();
    }

    @Inject
    private NativeHookPlugin nativeHookPlugin;
    @Inject
    private SettingsGateway settingsGateway;
    @Inject
    private ItemGateway itemGateway;

    private SettingsMenu settingsMenu;
    private Usecase usecase;

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
        settingsMenu = new SettingsMenu();
        settingsMenu.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        settingsMenu.init();
        settingsMenu.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent windowEvent) {
                settingsMenu.dispose();
                settingsMenu = null;
            }
        });
        settingsMenu.setVisible(true);
    }

    @Override
    public void nativeKeyTyped(NativeKeyEvent nativeKeyEvent) {
    }

    @Override
    public void nativeKeyPressed(NativeKeyEvent nativeKeyEvent) {
        int modifiers = nativeKeyEvent.getModifiers();
        if ((modifiers & CTRL_MASK) != 0 && (modifiers & SHIFT_MASK) != 0) {
            if (nativeKeyEvent.getKeyCode() == NativeKeyEvent.VC_A) {
                usecase = new AddItemsToStash();
            } else if (nativeKeyEvent.getKeyCode() == NativeKeyEvent.VC_V) {
                usecase = new IdentifyItems();
            }
        }
    }

    @Override
    public void nativeKeyReleased(NativeKeyEvent nativeKeyEvent) {
        if (usecase != null && nativeKeyEvent.getKeyCode() == NativeKeyEvent.VC_CONTROL) {
            usecase.execute();
            usecase = null;
        }
    }
}
