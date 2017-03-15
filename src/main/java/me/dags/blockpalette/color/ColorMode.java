package me.dags.blockpalette.color;

import me.dags.blockpalette.gui.Tooltip;
import net.minecraft.client.resources.I18n;

/**
 * @author dags <dags@dags.me>
 */
public enum ColorMode implements Tooltip.Provider {

    COMPLIMENTARY("palette.colormode.complimentary", "palette.tooltip.mode.complimentary"),
    ADJACENT("palette.colormode.adjacent", "palette.tooltip.mode.adjacent"),
    TRIAD("palette.colormode.triad", "palette.tooltip.mode.triad"),
    TETRAD("palette.colormode.tetrad", "palette.tooltip.mode.tetrad"),
    ;

    public final String display;
    public final String tooltip;

    ColorMode(String in, String tip) {
        this.display = in;
        this.tooltip = tip;
    }

    @Override
    public String getUnlocalized() {
        return tooltip;
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
