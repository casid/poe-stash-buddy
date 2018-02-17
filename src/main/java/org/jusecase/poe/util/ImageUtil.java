package org.jusecase.poe.util;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;

public class ImageUtil {

    public static final Kernel SOBEL_HORIZONTAL = new Kernel(3, 3, new float[]{
            1.0f, 0.0f, -1.0f,
            2.0f, 0.0f, -2.0f,
            1.0f, 0.0f, -1.0f
    });

    public static final Kernel SOBEL_VERTICAL = new Kernel(3, 3, new float[]{
            1.0f, 2.0f, 1.0f,
            0.0f, 0.0f, 0.0f,
            -1.0f, -2.0f, -1.0f
    });

    public static BufferedImage toGrayScale(BufferedImage source) {
        BufferedImage image = new BufferedImage(source.getWidth(), source.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
        Graphics g = image.getGraphics();
        g.drawImage(source, 0, 0, null);
        g.dispose();

        return image;
    }

    public static BufferedImage toFilteredImage(BufferedImage source, Kernel kernel) {
        return toFilteredImage(source, null, kernel);
    }

    public static BufferedImage toFilteredImage(BufferedImage source, BufferedImage destination, Kernel kernel) {
        ConvolveOp operation = new ConvolveOp(kernel, ConvolveOp.EDGE_ZERO_FILL, null);
        return operation.filter(source, destination);
    }
}
