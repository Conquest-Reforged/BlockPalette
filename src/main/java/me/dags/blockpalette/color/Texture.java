package me.dags.blockpalette.color;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;

/**
 * @author dags <dags@dags.me>
 */
public class Texture {

    public static final Texture EMPTY = new Texture();

    public final String name;
    final int red;
    final int green;
    final int blue;
    final int hue;
    final Float saturation;
    final Float lightness;
    final boolean hasTranslucency;
    final boolean hasTransparency;

    private final int rgDif;
    private final int gbDif;

    private Texture() {
        this.name = "EMPTY";
        this.red = 0;
        this.green = 0;
        this.blue = 0;
        this.hue = -1;
        this.saturation = 0F;
        this.lightness = 0F;
        this.hasTranslucency = true;
        this.hasTransparency = true;
        this.rgDif = 0;
        this.gbDif = 0;
    }


    Texture(String name, int width, int height, int[] data) {
        int r = 0, g = 0, b = 0, size = width * height;
        boolean translucent = false;
        boolean transparent = false;
        for (int i = 0; i < size; i++) {
            int c = data[i];
            int alpha = (c >> 24) & 0xff;

            if (alpha == 0) {
                size--;
                transparent = true;
            } else {
                r += c >> 16 & 0xFF;
                g += c >> 8 & 0xFF;
                b += c & 0xFF;
                if (alpha < 255) {
                    translucent = true;
                }
            }
        }

        size = Math.max(size, 1);
        r /= size;
        g /= size;
        b /= size;

        float rr = r / 255F;
        float gg = g / 255F;
        float bb = b / 255F;

        float max = Math.max(Math.max(rr, gg), bb);
        float min = Math.min(Math.min(rr, gg), bb);

        float hue;
        float lightness;
        float saturation;

        if (rr == max) {
            hue = ((gg - bb) / (max - min));
        } else if (gg == max) {
            hue = (2 + ((bb - rr) / (max - min)));
        } else {
            hue = (4 + ((rr - gg) / (max - min)));
        }

        hue = hue * 60;
        lightness = (min + max) / 2;
        saturation = min == max ? 0 : lightness > 0.5 ? (max - min) / (2F - max - min) : (max - min) / max + min;


        this.name = name;
        this.red = r;
        this.green = g;
        this.blue = b;
        this.hue = ColorWheel.clampHue(hue);
        this.lightness = lightness;
        this.saturation = saturation;
        this.hasTranslucency = translucent;
        this.hasTransparency = transparent;
        this.rgDif = Math.max(red, green) - Math.min(red, green);
        this.gbDif = Math.max(green, blue) - Math.min(green, blue);
    }

    public boolean isPresent() {
        return this != EMPTY;
    }

    public ColorF getColor() {
        return new ColorF(this);
    }

    boolean isGray(float grayPoint) {
        float point = grayPoint / lightness;
        return saturation < 0.5 && rgDif < point && gbDif < point;
    }

    @Override
    public String toString() {
        return String.format("%s rgb(%s,%s,%s) hue(%s) hsl(%s,%s,%s)", name, red, green, blue, hue, hue, saturation, lightness);
    }

    public static Texture fromSprite(TextureAtlasSprite sprite) {
        if (sprite.getFrameCount() > 0) {
            int[][] data = sprite.getFrameTextureData(0);
            if (data.length > 0) {
                int[] pixels = data[0];
                return new Texture(sprite.getIconName(), sprite.getIconWidth(), sprite.getIconHeight(), pixels);
            }
        }
        return null;
    }
}
