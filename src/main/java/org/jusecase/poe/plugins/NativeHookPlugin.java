package org.jusecase.poe.plugins;

import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.keyboard.NativeKeyListener;
import org.jusecase.inject.Component;

import java.util.logging.Level;
import java.util.logging.Logger;

@Component
public class NativeHookPlugin {
    public NativeHookPlugin() {
        adjustLogging();
    }

    private void adjustLogging() {
        Logger logger = Logger.getLogger(GlobalScreen.class.getPackage().getName());
        logger.setLevel(Level.WARNING);
        logger.setUseParentHandlers(false);
    }

    public void enable() {
        try {
            GlobalScreen.registerNativeHook();
        } catch (NativeHookException e) {
            throw new RuntimeException(e);
        }
    }

    public void disable() {
        try {
            GlobalScreen.unregisterNativeHook();
        } catch (NativeHookException e) {
            throw new RuntimeException(e);
        }
    }

    public void addListener(NativeKeyListener listener) {
        GlobalScreen.addNativeKeyListener(listener);
    }
}
