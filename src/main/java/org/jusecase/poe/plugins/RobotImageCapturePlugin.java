package org.jusecase.poe.plugins;

import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.awt.image.MultiResolutionImage;
import java.util.List;

public class RobotImageCapturePlugin implements ImageCapturePlugin {
    private final Robot robot;

    public RobotImageCapturePlugin() {
        try {
            robot = new Robot();
        } catch (AWTException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public BufferedImage captureScreen(Rectangle area) {
        Rectangle fullScreen = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
        MultiResolutionImage screenCapture = robot.createMultiResolutionScreenCapture(fullScreen);

        BufferedImage image;
        List<Image> resolutionVariants = screenCapture.getResolutionVariants();
        if (resolutionVariants.size() == 1) {
            image = (BufferedImage) resolutionVariants.get(0);
        } else {
            image = (BufferedImage) resolutionVariants.get(1);
        }

        return image.getSubimage(area.x, area.y, area.width, area.height);
    }
}
