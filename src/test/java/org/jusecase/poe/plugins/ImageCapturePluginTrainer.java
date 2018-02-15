package org.jusecase.poe.plugins;

import org.jusecase.Builders;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import static org.jusecase.Builders.a;
import static org.jusecase.Builders.inputStream;

public class ImageCapturePluginTrainer implements ImageCapturePlugin {

    private BufferedImage image;
    private List<BufferedImage> images = new ArrayList<>();

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

    public void givenImages(String ... resources) {
        for (String resource : resources) {
            givenImage(resource);
            images.add(image);
        }
    }

    @Override
    public BufferedImage captureScreen(Rectangle area) {
        if (images.isEmpty()) {
            return image;
        }
        return images.remove(0);
    }
}