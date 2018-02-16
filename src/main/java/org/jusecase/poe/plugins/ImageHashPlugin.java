package org.jusecase.poe.plugins;

import org.jusecase.poe.entities.Hash;
import org.jusecase.poe.plugins.phash.ImageHash;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

public class ImageHashPlugin {
    private static final int SIZE = 32;
    private static final int SMALL_SIZE = 8;

    public static final int HASH_LENGTH = SMALL_SIZE * SMALL_SIZE;

    private ImageHash imageHash = new ImageHash(SIZE, SMALL_SIZE);

    public Hash getHash(InputStream inputStream) throws IOException {
        return getHash(inputStream, null);
    }

    public Hash getHash(InputStream inputStream, Color backgroundColor) throws IOException {
        try {
            return imageHash.getHash(ImageIO.read(inputStream), backgroundColor);
        } finally {
            inputStream.close();
        }
    }

    public Hash getHash(BufferedImage image) {
        return imageHash.getHash(image, null);
    }

    public boolean isSimilar(Hash hash1, Hash hash2) {
        return isSimilar(hash1, hash2, false);
    }

    public boolean isSimilar(Hash hash1, Hash hash2, boolean debug) {
        double features = getNormalizedDistance(hash1.features, hash2.features);
        double colors = getNormalizedDistance(hash1.colors, hash2.colors);

        if (debug) {
            System.out.println("Features: " + features + ", Colors: " + colors);
        }

        return features < 0.18 && colors < 0.2;
    }

    public double getNormalizedDistance(String hash1, String hash2) {
        double distance = getDistance(hash1, hash2);
        return distance / hash1.length();
    }

    public int getDistance(String hash1, String hash2) {
        return imageHash.distance(hash1, hash2);
    }
}
