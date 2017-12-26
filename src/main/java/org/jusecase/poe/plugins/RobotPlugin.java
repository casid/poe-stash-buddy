package org.jusecase.poe.plugins;

import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;

public class RobotPlugin implements InputPlugin, ImageCapturePlugin {
    private final Robot robot;

    public RobotPlugin() {
        try {
            robot = new Robot();
        } catch (AWTException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void click(int x, int y) {
        robot.mouseMove(x, y);
        robot.mousePress(InputEvent.BUTTON1_MASK);
        robot.mouseRelease(InputEvent.BUTTON1_MASK);
    }

    @Override
    public void clickWithControlPressed(int x, int y) {
        robot.mouseMove(x, y);
        robot.keyPress(KeyEvent.VK_CONTROL);
        robot.mousePress(InputEvent.BUTTON1_MASK);
        robot.mouseRelease(InputEvent.BUTTON1_MASK);
        robot.keyRelease(KeyEvent.VK_CONTROL);
    }

    @Override
    public BufferedImage captureScreen(Rectangle area) {
        return robot.createScreenCapture(area);
    }
}
