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
        double features = getNormalizedDistance(hash1.features, hash2.features);
        double colors1 = getNormalizedDistance(hash1.colors1, hash2.colors1);
        double colors2 = getNormalizedDistance(hash1.colors2, hash2.colors2);

        return features < 0.18 && (colors1 < 0.18 || colors2 < 0.18);
    }

    public String describeDistance(Hash hash1, Hash hash2) {
        String description = "Actual normalized distance is ";
        description += "f: " + getNormalizedDistance(hash1.features, hash2.features) + ", ";
        description += "c1: " + getNormalizedDistance(hash1.colors1, hash2.colors1) + ", ";
        description += "c2: " + getNormalizedDistance(hash1.colors2, hash2.colors2);

        description += " (absolute distance f:" + getDistance(hash1.features, hash2.features) + ", ";
        description += "c1:" + getDistance(hash1.colors1, hash2.colors1) + ", ";
        description += "c2:" + getDistance(hash1.colors2, hash2.colors2) + ")";

        return description;
    }

    public double getNormalizedDistance(String hash1, String hash2) {
        double distance = getDistance(hash1, hash2);
        return distance / hash1.length();
    }

    public int getDistance(String hash1, String hash2) {
        return imageHash.distance(hash1, hash2);
    }
}
