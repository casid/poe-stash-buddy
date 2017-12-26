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

    public final int size;
    public final int smallerSize;

    public ImageHash(int size, int smallerSize) {
        this.size = size;
        this.smallerSize = smallerSize;

        initCoefficients();
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

    public String getHash(InputStream is) throws IOException {
        BufferedImage img = ImageIO.read(is);
        return getHash(img);
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

        return getHashForChannel(img, 0) + getHashForChannel(img, 1) + getHashForChannel(img, 2);
    }

    private String getHashForChannel(BufferedImage img, int channel) {
        double[][] vals = new double[size][size];

        for (int x = 0; x < img.getWidth(); x++) {
            for (int y = 0; y < img.getHeight(); y++) {
                if (channel == 0) {
                    vals[x][y] = getRed(img, x, y);
                } else if (channel == 1) {
                    vals[x][y] = getGreen(img, x, y);
                } else {
                    vals[x][y] = getBlue(img, x, y);
                }
            }
        }

                /* 3. Compute the DCT.
                 * The DCT separates the image into a collection of frequencies
                 * and scalars. While JPEG uses an 8x8 DCT, this algorithm uses
                 * a 32x32 DCT.
                 */
        double[][] dctVals = applyDCT(vals);

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
        double total = 0;

        for (int x = 0; x < smallerSize; x++) {
            for (int y = 0; y < smallerSize; y++) {
                total += dctVals[x][y];
            }
        }

        double avg = total / (double) ((smallerSize * smallerSize) - 1);

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
                if (x != 0 || y != 0) {
                    hash.append(dctVals[x][y] > avg ? "1" : "0");
                }
            }
        }

        return hash.toString();
    }

    private BufferedImage resize(BufferedImage image, int width, int height) {
        BufferedImage resizedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = resizedImage.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        if (image.getColorModel().hasAlpha()) {
            g.setBackground(new Color(4, 4, 30));
            g.clearRect(0, 0, width, height);
        }

        g.drawImage(image, 0, 0, width, height, null);

        g.clearRect(0, 0, width / 2, height / 2); // cut off text

        g.dispose();
        return resizedImage;
    }

    private static int getBlue(BufferedImage img, int x, int y) {
        return (img.getRGB(x, y)) & 0xff;
    }

    private static int getRed(BufferedImage img, int x, int y) {
        return (img.getRGB(x, y) >> 16) & 0xff;
    }

    private static int getGreen(BufferedImage img, int x, int y) {
        return (img.getRGB(x, y) >> 8) & 0xff;
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

    private double[][] applyDCT(double[][] f) {
        int N = size;

        double[][] F = new double[N][N];
        for (int u = 0; u < smallerSize; u++) {
            for (int v = 0; v < smallerSize; v++) {
                double sum = 0.0;
                if (u != 0 || v != 0) {
                    for (int i = 0; i < N; i++) {
                        for (int j = 0; j < N; j++) {
                            sum += Math.cos(((2 * i + 1) / (2.0 * N)) * u * Math.PI) * Math.cos(((2 * j + 1) / (2.0 * N)) * v * Math.PI) * (f[i][j]);
                        }
                    }
                    sum *= ((c[u] * c[v]) / 4.0);
                }
                F[u][v] = sum;
            }
        }
        return F;
    }
}
