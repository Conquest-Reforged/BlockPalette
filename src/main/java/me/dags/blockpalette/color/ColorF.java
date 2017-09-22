package me.dags.blockpalette.color;

import java.awt.*;

/**
 * @author dags <dags@dags.me>
 */
public class ColorF {

    public static final ColorF EMPTY = new ColorF(0.35F, 0.35F, 0.35F);

    public final float red;
    public final float green;
    public final float blue;

    ColorF(Color color) {
        red = color.getRed() / 255F;
        green = color.getGreen() / 255F;
        blue = color.getBlue() / 255F;
    }

    ColorF(Texture texture) {
        red = texture.red;
        green = texture.green;
        blue = texture.blue;
    }

    public ColorF(float r, float g, float b) {
        this.red = r;
        this.green = g;
        this.blue = b;
    }

    private ColorF() {
        red = 1F;
        green = 1F;
        blue = 1F;
    }

    public int toARGB(float opacity) {
        int argb = (int) (opacity * 255);
        argb = (argb << 8) + (int) (red * 255);
        argb = (argb << 8) + (int) (green * 255);
        argb = (argb << 8) + (int) (blue * 255);
        return argb;
    }

    public int colorCode() {
        return rgb(Math.round(red * 255), Math.round(green * 255), Math.round(blue * 255));
    }

    public static int rgb(int r, int g, int b) {
        int rgb = r;
        rgb = (rgb << 8) + g;
        rgb = (rgb << 8) + b;
        return rgb;
    }
}
