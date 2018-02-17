package org.jusecase.poe.plugins.phash;

import org.jusecase.poe.entities.Hash;
import org.jusecase.poe.util.ColorUtil;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

/*
 * pHash-like image hash.
 * Author: Elliot Shepherd (elliot@jarofworms.com
 * Based On: http://www.hackerfactor.com/blog/index.php?/archives/432-Looks-Like-It.html
 */
public class ImageHash {

    private final int size;
    private final int smallerSize;
    private final BufferedImage background;
    private final BufferedImage backgroundOverlay;

    public ImageHash(int size, int smallerSize) {
        this.size = size;
        this.smallerSize = smallerSize;
        this.background = loadBackground();
        this.backgroundOverlay = loadBackgroundOverlay();

        initCoefficients();
        initCosineLookup();
    }

    private BufferedImage loadBackgroundOverlay() {
        return background.getSubimage(0, 0, background.getWidth() / 2, background.getHeight() / 2);
    }

    private BufferedImage loadBackground() {
        try(InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("item-slot-bg.png")) {
            return ImageIO.read(inputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public int distance(String s1, String s2) {
        int counter = 0;
        for (int k = 0; k < s1.length(); k++) {
            if (s1.charAt(k) != s2.charAt(k)) {
                counter++;
            }
        }
        return counter;
    }

    public Hash getHash(BufferedImage img, Color backgroundColor) {

        img = resize(img, size, size, backgroundColor);

        double[][] features = new double[size][size];
        double[][] colors1 = new double[size][size];
        double[][] colors2 = new double[size][size];
        float[] hsb = new float[3];
        for (int x = 0; x < size; ++x) {
            for (int y = 0; y < size; ++y) {

                int rgb = img.getRGB(x, y);

                int r = (rgb >> 16) & 0xff;
                int g = (rgb >> 8) & 0xff;
                int b = rgb & 0xff;

                Color.RGBtoHSB(r, g, b, hsb);

                colors1[x][y] = calculateColors1(hsb);
                colors2[x][y] = calculateColors2(hsb);
                features[x][y] = hsb[2];
            }
        }

        double[][] featuresDct = applyDCT(features);
        double[][] colors1Dct = applyDCT(colors1);
        double[][] colors2Dct = applyDCT(colors2);

        Hash hash = new Hash();
        hash.features = getHash(featuresDct);
        hash.colors1 = getHash(colors1Dct);
        hash.colors2 = getHash(colors2Dct);
        return hash;
    }

    private float calculateColors1(float[] hsb) {
        return stretchHue(hsb[0]) * hsb[1];
    }

    private float calculateColors2(float[] hsb) {
        float hue = ColorUtil.applyHueOffset(hsb[0], + 0.5f);
        return stretchHue(hue) * hsb[1];
    }

    private float stretchHue(float hue) {
        return 0.8f * hue + 0.2f;
    }

    private String getHash(double[][] values) {
        StringBuilder hash = new StringBuilder();

        double average = getAverage(values);
        for (int x = 0; x < smallerSize; x++) {
            for (int y = 0; y < smallerSize; y++) {
                hash.append(values[x][y] > average ? "1" : "0");
            }
        }

        return hash.toString();
    }

    private double getAverage(double[][] dctVals) {
        double total = 0;

        for (int x = 0; x < smallerSize; x++) {
            for (int y = 0; y < smallerSize; y++) {
                total += dctVals[x][y];
            }
        }

        return total / (double) ((smallerSize * smallerSize) - 1);
    }

    private BufferedImage resize(BufferedImage image, int width, int height, Color backgroundColor) {
        BufferedImage resizedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = resizedImage.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        if (image.getColorModel().hasAlpha()) {
            if (backgroundColor != null) {
                g.setBackground(backgroundColor);
                g.clearRect(0, 0, width, height);
            } else {
                g.drawImage(background, 0, 0, width, height, null);
            }
        }

        g.drawImage(image, 0, 0, width, height, null);

        // cut off text
        g.drawImage(backgroundOverlay, 0, 0, backgroundOverlay.getWidth(), backgroundOverlay.getHeight(), null);

        g.dispose();
        return resizedImage;
    }

    // DCT function stolen from http://stackoverflow.com/questions/4240490/problems-with-dct-and-idct-algorithm-in-java

    private double[] c;
    private double[] cosineLookup;

    private void initCoefficients() {
        c = new double[size];

        for (int i = 1; i < size; i++) {
            c[i] = 1;
        }
        c[0] = 1 / Math.sqrt(2.0);
    }

    private void initCosineLookup() {
        cosineLookup = new double[size * smallerSize];
        for (int i = 0; i < size; i++) {
            for (int u = 0; u < smallerSize; u++) {
                cosineLookup[i * smallerSize + u] = Math.cos(((2 * i + 1) / (2.0 * size)) * u * Math.PI);
            }
        }
    }

    private double[][] applyDCT(double[][] f) {
        int N = size;

        double[][] F = new double[smallerSize][smallerSize];
        for (int u = 0; u < smallerSize; u++) {
            for (int v = 0; v < smallerSize; v++) {
                double sum = 0.0;
                if (u != 0 || v != 0) {
                    for (int i = 0; i < N; i++) {
                        for (int j = 0; j < N; j++) {
                            sum += cosineLookup[i * smallerSize + u] * cosineLookup[j * smallerSize + v] * f[i][j];
                        }
                    }
                    sum *= ((c[u] * c[v]) / N);
                }
                F[u][v] = sum;
            }
        }
        return F;
    }
}
