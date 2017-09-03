package me.dags.blockpalette.color;

import java.awt.*;

/**
 * @author dags <dags@dags.me>
 */
public enum ColorConst {
    BLUE(Color.BLUE, "blue"),
    CYAN(Color.CYAN, "cyan"),
    GRAY(Color.DARK_GRAY, "gray"),
    GREEN(Color.GREEN, "green"),
    MAGENTA(Color.MAGENTA, "magenta"),
    ORANGE(Color.ORANGE, "orange"),
    PINK(Color.PINK, "pink"),
    RED(Color.RED, "red"),
    WHITE(Color.WHITE, "white"),
    YELLOW(Color.YELLOW, "yellow"),;

    private final ColorF color;
    private final String name;

    ColorConst(Color color, String name) {
        this.color = new ColorF(color);
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

    public static ColorConst nearest(ColorF color) {
        ColorConst[] values = values();
        ColorConst result = values[0];
        double min = Double.MAX_VALUE;
        for (int i = 1; i < values.length; i++) {
            ColorConst c = values[i];
            if (c == GRAY) {
                continue;
            }
            double dist2 = distanceSq(color, c.color);
            if (dist2 < min) {
                min = dist2;
                result = c;
            }
        }
        return result;
    }

    private static double distanceSq(ColorF c1, ColorF c2) {
        float dr = c1.red - c2.red;
        float dg = c1.green - c2.green;
        float db = c1.blue - c2.blue;
        return (dr * dr * 0.2989F) + (dg * dg * 0.5870F) + (db * db * 0.1140F);
    }
}
