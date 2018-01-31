package org.jusecase.poe.plugins;

import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;
import org.jnativehook.mouse.NativeMouseEvent;
import org.jusecase.inject.Component;
import org.jusecase.poe.gateways.SettingsGateway;

import javax.inject.Inject;
import java.util.logging.Level;
import java.util.logging.Logger;

@Component
public class NativeHookPlugin implements InputPlugin {

    @Inject
    SettingsGateway settingsGateway;

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

    @Override
    public void wait(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            wait(millis);
        }
    }

    @Override
    public void waitDefaultTime() {
        waitDefaultTime(1);
    }

    @Override
    public void waitDefaultTime(int factor) {
        wait(factor * settingsGateway.getSettings().inputDelayMillis);
    }

    @Override
    public void click(int x, int y) {
        mouseMove(x, y);
        waitDefaultTime();
        mousePress(x, y);
        waitDefaultTime();
        mouseRelease(x, y);
        waitDefaultTime();
    }

    @Override
    public void clickWithControlPressed(int x, int y) {
        sendControlKeyEvent(NativeKeyEvent.NATIVE_KEY_PRESSED);
        click(x, y);
        sendControlKeyEvent(NativeKeyEvent.NATIVE_KEY_RELEASED);
    }

    private void sendControlKeyEvent(int nativeKeyPressed) {
        GlobalScreen.postNativeEvent(new NativeKeyEvent(
                nativeKeyPressed,
                0x00,        // Modifiers
                0x00,        // Raw Code
                NativeKeyEvent.VC_CONTROL,
                NativeKeyEvent.CHAR_UNDEFINED,
                NativeKeyEvent.KEY_LOCATION_UNKNOWN));
    }

    private void mousePress(int x, int y) {
        GlobalScreen.postNativeEvent(new NativeMouseEvent(
                NativeMouseEvent.NATIVE_MOUSE_PRESSED,
                0,
                x,
                y,
                1,
                NativeMouseEvent.BUTTON1));
    }

    private void mouseRelease(int x, int y) {
        GlobalScreen.postNativeEvent(new NativeMouseEvent(
                NativeMouseEvent.NATIVE_MOUSE_RELEASED,
                0,
                x,
                y,
                1,
                NativeMouseEvent.BUTTON1));
    }

    private void mouseMove(int x, int y) {
        GlobalScreen.postNativeEvent(new NativeMouseEvent(
                NativeMouseEvent.NATIVE_MOUSE_MOVED,
                0,
                x,
                y,
                0,
                NativeMouseEvent.NOBUTTON));
    }
}
