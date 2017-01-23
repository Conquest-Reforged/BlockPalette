package me.dags.blockpalette.color;

/**
 * @author dags <dags@dags.me>
 */
public class Texture {

    public static final Texture EMPTY = new Texture();

    public final String name;
    final int hue;
    final float red;
    final float green;
    final float blue;
    final Float saturation;
    final Float brightness;
    final Float luminosity;
    final Float strength;

    private Texture() {
        this.name = "EMPTY";
        this.hue = -1;
        this.red = 0F;
        this.green = 0F;
        this.blue = 0F;
        this.saturation = 0F;
        this.brightness = 0F;
        this.luminosity = 0F;
        this.strength = 0F;
    }


    public Texture(String name, int width, int height, int[] data) {
        int r = 0, g = 0, b = 0, size = width * height;

        for (int i = 0; i < size; i++) {
            int c = data[i];
            int alpha = (c >> 24) & 0xff;
            if (alpha == 0) {
                size--;
            } else {
                r += c >> 16 & 0xFF;
                g += c >> 8 & 0xFF;
                b += c & 0xFF;
            }
        }

        size = Math.max(size, 1);
        r /= size;
        g /= size;
        b /= size;

        this.red = r / 255F;
        this.green = g / 255F;
        this.blue = b / 255F;

        float[] hsb = RGBtoHSB(red, green, blue);

        this.name = name;
        this.hue = Math.round(hsb[0]);
        this.saturation = hsb[1];
        this.brightness = hsb[2];
        this.luminosity = 0.2126F * red + 0.715F * green + 0.0722F * blue;
        this.strength = brightness * saturation;
    }

    public boolean isPresent() {
        return this != EMPTY;
    }

    public ColorF getColor() {
        return new ColorF(this);
    }

    @Override
    public String toString() {
        return String.format("%s rgb(%s,%s,%s) hue(%s) hsb(%s,%s,%s)", name, red, green, blue, hue, hue, saturation, brightness);
    }

    // com.sun.javafx.util.Utils.RGBtoHSB(..)
    private static float[] RGBtoHSB(float r, float g, float b) {
        float hue, saturation, brightness;
        float[] hsbvals = new float[3];
        float cmax = (r > g) ? r : g;
        if (b > cmax) cmax = b;
        float cmin = (r < g) ? r : g;
        if (b < cmin) cmin = b;

        brightness = cmax;
        if (cmax != 0)
            saturation = (cmax - cmin) / cmax;
        else
            saturation = 0;

        if (saturation == 0) {
            hue = 0;
        } else {
            float redc = (cmax - r) / (cmax - cmin);
            float greenc = (cmax - g) / (cmax - cmin);
            float bluec = (cmax - b) / (cmax - cmin);
            if (r == cmax)
                hue = bluec - greenc;
            else if (g == cmax)
                hue = 2.0F + redc - bluec;
            else
                hue = 4.0F + greenc - redc;
            hue = hue / 6.0F;
            if (hue < 0)
                hue = hue + 1.0F;
        }
        hsbvals[0] = hue * 360F;
        hsbvals[1] = saturation;
        hsbvals[2] = brightness;

        return hsbvals;
    }
}
