package org.jusecase.poe.plugins;

import org.jusecase.poe.plugins.phash.ImageHash;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

public class ImageHashPlugin {
    private static final int SIZE = 32;
    private static final int SMALL_SIZE = 8;

    public static final int HASH_LENGTH = SMALL_SIZE * SMALL_SIZE;

    private ImageHash imageHash = new ImageHash(SIZE, SMALL_SIZE);

    public String getHash(InputStream inputStream) throws IOException {
        return imageHash.getHash(inputStream);
    }

    public String getHash(BufferedImage image) throws IOException {
        return imageHash.getHash(image);
    }

    public boolean isSimilar(String hash1, String hash2) {
        return getNormalizedDistance(hash1, hash2) < 0.12;
    }

    public double getNormalizedDistance(String hash1, String hash2) {
        double distance = getDistance(hash1, hash2);
        return distance / HASH_LENGTH;
    }

    public int getDistance(String hash1, String hash2) {
        return imageHash.distance(hash1, hash2);
    }
}
