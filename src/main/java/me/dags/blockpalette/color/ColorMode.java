package me.dags.blockpalette.color;

/**
 * @author dags <dags@dags.me>
 */
public enum ColorMode {

    COMPLIMENTARY("Complimentary"),
    ADJACENT("Adjacent"),
    TRIAD("Triad"),
    TETRAD("Tetrad"),
    ;

    public final String display;

    ColorMode(String in) {
        this.display = in;
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
