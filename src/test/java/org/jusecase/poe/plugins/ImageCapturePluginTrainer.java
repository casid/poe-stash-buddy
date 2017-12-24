package org.jusecase.poe.plugins;

import org.jusecase.Builders;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

import static org.jusecase.Builders.a;
import static org.jusecase.Builders.inputStream;

public class ImageCapturePluginTrainer implements ImageCapturePlugin {

    private BufferedImage image;

    public void givenImage(BufferedImage image) {
        this.image = image;
    }

    public void givenImage(String resource) {
        try {
            givenImage(ImageIO.read(a(inputStream().withResource(resource))));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public BufferedImage captureScreen(Rectangle area) {
        return image;
    }
}