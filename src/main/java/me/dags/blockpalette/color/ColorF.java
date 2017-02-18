package me.dags.blockpalette.color;

/**
 * @author dags <dags@dags.me>
 */
public class ColorF {

    public static final ColorF EMPTY = new ColorF(0.35F, 0.35F, 0.35F);

    public final float red;
    public final float green;
    public final float blue;

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
