package org.jusecase.poe.plugins;

import org.jusecase.poe.entities.Hash;
import org.jusecase.poe.plugins.phash.ImageHash;
import sun.nio.ch.IOUtil;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

public class ImageHashPlugin {
    private static final int SIZE = 32;
    private static final int SMALL_SIZE = 8;

    public static final int HASH_LENGTH = SMALL_SIZE * SMALL_SIZE;

    private ImageHash imageHash = new ImageHash(SIZE, SMALL_SIZE);

    public Hash getHash(InputStream inputStream) throws IOException {
        try {
            return imageHash.getHash(ImageIO.read(inputStream));
        } finally {
            inputStream.close();
        }
    }

    public Hash getHash(BufferedImage image) throws IOException {
        return imageHash.getHash(image);
    }

    public boolean isSimilar(Hash hash1, Hash hash2) {
        return getNormalizedDistance(hash1.features, hash2.features) < 0.18 &&
               getNormalizedDistance(hash1.colors, hash2.colors) < 0.18;
    }

    public double getNormalizedDistance(String hash1, String hash2) {
        double distance = getDistance(hash1, hash2);
        return distance / hash1.length();
    }

    public int getDistance(String hash1, String hash2) {
        return imageHash.distance(hash1, hash2);
    }
}
