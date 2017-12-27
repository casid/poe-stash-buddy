package org.jusecase.poe.plugins.phash;

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

    public ImageHash(int size, int smallerSize) {
        this.size = size;
        this.smallerSize = smallerSize;
        this.background = loadBackground();

        initCoefficients();
    }

    private BufferedImage loadBackground() {
        try {
            return ImageIO.read(Thread.currentThread().getContextClassLoader().getResourceAsStream("item-slot-bg.png"));
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

    // Returns a 'binary string' (like. 001010111011100010) which is easy to do a hamming distance on.
    public String getHash(BufferedImage img) throws IOException {


                /* 1. Reduce size.
                 * Like Average Hash, pHash starts with a small image.
                 * However, the image is larger than 8x8; 32x32 is a good size.
                 * This is really done to simplify the DCT computation and not
                 * because it is needed to reduce the high frequencies.
                 */
        img = resize(img, size, size);

        double[][] hsv = new double[size][size];
        double[][] bv = new double[size][size];
        float[] hsb = new float[3];
        for (int x = 0; x < size; ++x) {
            for (int y = 0; y < size; ++y) {

                int rgb = img.getRGB(x, y);

                int r = (rgb >> 16) & 0xff;
                int g = (rgb >> 8) & 0xff;
                int b = rgb & 0xff;

                Color.RGBtoHSB(r, g, b, hsb);

                hsv[x][y] = hsb[0] * hsb[1];
                bv[x][y] = hsb[2];
            }
        }

        double[][] hsvDct = applyAverage(hsv);
        double[][] bvDct = applyDCT(bv);

        return getHash(hsvDct) + getHash(bvDct);
    }

    private String getHash(double[][] dctVals) throws IOException {

                /* 4. Reduce the DCT.
                 * This is the magic step. While the DCT is 32x32, just keep the
                 * top-left 8x8. Those represent the lowest frequencies in the
                 * picture.
                 */
                /* 5. Compute the average value.
                 * Like the Average Hash, compute the mean DCT value (using only
                 * the 8x8 DCT low-frequency values and excluding the first term
                 * since the DC coefficient can be significantly different from
                 * the other values and will throw off the average).
                 */
        double avg = getAverage(dctVals);

                /* 6. Further reduce the DCT.
                 * This is the magic step. Set the 64 hash bits to 0 or 1
                 * depending on whether each of the 64 DCT values is above or
                 * below the average value. The result doesn't tell us the
                 * actual low frequencies; it just tells us the very-rough
                 * relative scale of the frequencies to the mean. The result
                 * will not vary as long as the overall structure of the image
                 * remains the same; this can survive gamma and color histogram
                 * adjustments without a problem.
                 */
        StringBuilder hash = new StringBuilder();

        for (int x = 0; x < smallerSize; x++) {
            for (int y = 0; y < smallerSize; y++) {
                hash.append(dctVals[x][y] > avg ? "1" : "0");
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

    private double getDifference(double[][] dctVals) {
        double min = Double.MAX_VALUE;
        double max = Double.MIN_VALUE;

        for (int x = 0; x < smallerSize; x++) {
            for (int y = 0; y < smallerSize; y++) {
                double value = dctVals[x][y];
                if (value < min) {
                    min = value;
                }
                if (value > max) {
                    max = value;
                }
            }
        }

        return Math.abs(max - min);
    }

    private BufferedImage resize(BufferedImage image, int width, int height) {
        BufferedImage resizedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = resizedImage.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        if (image.getColorModel().hasAlpha()) {
            g.drawImage(background, 0, 0, width, height, null);
        }

        g.drawImage(image, 0, 0, width, height, null);

        g.setBackground(new Color(4, 4, 30));
        g.clearRect(0, 0, width / 2, height / 2); // cut off text

        g.dispose();
        return resizedImage;
    }

    // DCT function stolen from http://stackoverflow.com/questions/4240490/problems-with-dct-and-idct-algorithm-in-java

    private double[] c;

    private void initCoefficients() {
        c = new double[size];

        for (int i = 1; i < size; i++) {
            c[i] = 1;
        }
        c[0] = 1 / Math.sqrt(2.0);
    }

    private double[][] applyAverage(double[][] f) {
        double[][] a = new double[smallerSize][smallerSize];
        for (int x = 0; x < smallerSize; ++x) {
            for (int y = 0; y < smallerSize; ++y) {
                int fx = 4 * x;
                int fy = 4 * y;

                a[x][y] = 0.25 *(f[fx][fy] + f[fx+1][fy] + f[fx][fy+1] + f[fx+1][fy+1]);
            }
        }
        return a;
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
                            sum += Math.cos(((2 * i + 1) / (2.0 * N)) * u * Math.PI) * Math.cos(((2 * j + 1) / (2.0 * N)) * v * Math.PI) * (f[i][j]);
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
