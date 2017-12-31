package org.jusecase.poe.plugins;

import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.keyboard.NativeKeyListener;
import org.jnativehook.mouse.NativeMouseEvent;
import org.jusecase.inject.Component;

import java.util.logging.Level;
import java.util.logging.Logger;

@Component
public class NativeHookPlugin implements InputPlugin {

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
    public void click(int x, int y) {
        mouseMove(x, y, 0);
        wait(DELAY);
        mousePress(x, y, 0);
        wait(DELAY);
        mouseRelease(x, y, 0);
        wait(DELAY_AFTER_CLICK);
    }

    @Override
    public void clickWithControlPressed(int x, int y) {
        mouseMove(x, y, NativeMouseEvent.CTRL_L_MASK);
        wait(DELAY);
        mousePress(x, y, NativeMouseEvent.CTRL_L_MASK);
        wait(DELAY);
        mouseRelease(x, y, NativeMouseEvent.CTRL_L_MASK);
        wait(DELAY_AFTER_CLICK);
    }

    private void mousePress(int x, int y, int modifiers) {
        GlobalScreen.postNativeEvent(new NativeMouseEvent(
                NativeMouseEvent.NATIVE_MOUSE_PRESSED,
                modifiers,
                x,
                y,
                1,
                NativeMouseEvent.BUTTON1));
    }

    private void mouseRelease(int x, int y, int modifiers) {
        GlobalScreen.postNativeEvent(new NativeMouseEvent(
                NativeMouseEvent.NATIVE_MOUSE_RELEASED,
                modifiers,
                x,
                y,
                1,
                NativeMouseEvent.BUTTON1));
    }

    private void mouseMove(int x, int y, int modifiers) {
        GlobalScreen.postNativeEvent(new NativeMouseEvent(
                NativeMouseEvent.NATIVE_MOUSE_MOVED,
                modifiers,
                x,
                y,
                1,
                NativeMouseEvent.NOBUTTON));
    }
}
