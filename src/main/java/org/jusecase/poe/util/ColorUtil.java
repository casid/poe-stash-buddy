package org.jusecase.poe.util;

import java.awt.*;

public class ColorUtil {

    @SuppressWarnings("unused") // Did not work out as expected, but maybe we need this in the future again
    public static float getHsvDistance(Color color1, Color color2, float hueOffset) {
        float[] hsv1 = Color.RGBtoHSB(color1.getRed(), color1.getGreen(), color1.getBlue(), null);
        float[] hsv2 = Color.RGBtoHSB(color2.getRed(), color2.getGreen(), color2.getBlue(), null);

        hsv1[0] = applyHueOffset(hsv1[0], hueOffset);
        hsv2[0] = applyHueOffset(hsv2[0], hueOffset);

        float dh = hsv2[0] - hsv1[0];
        float ds = hsv2[1] - hsv1[1];
        float dv = hsv2[2] - hsv1[2];

        return (float)Math.sqrt(dh * dh + ds * ds + dv * dv);
    }

    public static float applyHueOffset(float hue, float offset) {
        hue += offset;
        if (hue > 1.0f) {
            hue -= 1.0f;
        }
        return Math.max(0.0f, hue);
    }
}
