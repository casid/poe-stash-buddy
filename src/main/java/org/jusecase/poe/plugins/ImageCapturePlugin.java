package org.jusecase.poe.plugins;

import java.awt.*;
import java.awt.image.BufferedImage;

public interface ImageCapturePlugin {
    BufferedImage captureScreen(Rectangle area);
}
