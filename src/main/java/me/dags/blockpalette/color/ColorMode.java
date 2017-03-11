package me.dags.blockpalette.color;

import net.minecraft.client.resources.I18n;

/**
 * @author dags <dags@dags.me>
 */
public enum ColorMode {

    COMPLIMENTARY("palette.colormode.complimentary"),
    ADJACENT("palette.colormode.adjacent"),
    TRIAD("palette.colormode.triad"),
    TETRAD("palette.colormode.tetrad"),
    ;

    public final String display;

    ColorMode(String in) {
        this.display = in;
    }

    @Override
    public String toString() {
        return I18n.format(display);
    }

    public static int maxId() {
        return ColorMode.values().length - 1;
    }

    public static int getId(ColorMode mode) {
        switch (mode) {
            case ADJACENT:
                return 1;
            case TRIAD:
                return 2;
            case TETRAD:
                return 3;
            default:
                return 0;
        }
    }

    public static ColorMode fromId(int id) {
        switch (id) {
            case 1:
                return ADJACENT;
            case 2:
                return TRIAD;
            case 3:
                return TETRAD;
            default:
                return COMPLIMENTARY;
        }
    }

    public static ColorMode next(ColorMode mode) {
        switch (mode) {
            case ADJACENT:
                return TRIAD;
            case TRIAD:
                return TETRAD;
            case TETRAD:
                return COMPLIMENTARY;
            default:
                return ADJACENT;
        }
    }
}
