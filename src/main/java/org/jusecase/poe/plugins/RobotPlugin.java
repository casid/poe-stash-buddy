package org.jusecase.poe.plugins;

import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;

public class RobotPlugin implements InputPlugin, ImageCapturePlugin {
    private static final int DELAY = 50;

    private final Robot robot;

    public RobotPlugin() {
        try {
            robot = new Robot();
        } catch (AWTException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void wait(int millis) {
        robot.delay(millis);
    }

    @Override
    public void click(int x, int y) {
        mouseMove(x, y);
        robot.delay(DELAY);
        robot.mousePress(InputEvent.BUTTON1_MASK);
        robot.delay(DELAY);
        robot.mouseRelease(InputEvent.BUTTON1_MASK);
    }

    @Override
    public void clickWithControlPressed(int x, int y) {
        mouseMove(x, y);
        robot.delay(DELAY);
        robot.keyPress(KeyEvent.VK_CONTROL);
        robot.mousePress(InputEvent.BUTTON1_MASK);
        robot.delay(DELAY);
        robot.mouseRelease(InputEvent.BUTTON1_MASK);
        robot.keyRelease(KeyEvent.VK_CONTROL);
    }

    private void mouseMove(int x, int y) {
        // shit, this is broken on windows.
        for (int i = 0; i < 15; ++i) {
            robot.mouseMove(x, y);
        }
    }

    @Override
    public BufferedImage captureScreen(Rectangle area) {
        return robot.createScreenCapture(area);
    }
}
