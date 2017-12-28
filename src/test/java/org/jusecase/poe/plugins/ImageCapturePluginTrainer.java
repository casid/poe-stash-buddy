package org.jusecase.poe.plugins;

import org.jusecase.Builders;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

import static org.jusecase.Builders.a;
import static org.jusecase.Builders.inputStream;

public class ImageCapturePluginTrainer implements ImageCapturePlugin {

    private BufferedImage image;

    public void givenImage(BufferedImage image) {
        this.image = image;
    }

    public void givenImage(String resource) {
        try(InputStream inputStream = a(inputStream().withResource(resource))) {
            givenImage(ImageIO.read(inputStream));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public BufferedImage captureScreen(Rectangle area) {
        return image;
    }
}