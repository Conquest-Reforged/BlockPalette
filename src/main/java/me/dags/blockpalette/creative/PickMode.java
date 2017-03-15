package me.dags.blockpalette.creative;

import me.dags.blockpalette.gui.Tooltip;
import net.minecraft.client.resources.I18n;

/**
 * @author dags <dags@dags.me>
 */
public enum PickMode implements Tooltip.Provider {
    KEYBOARD("palette.pickmode.keyboard", "palette.tooltip.pickmode.keyboard"),
    MOUSE("palette.pickmode.mouse", "palette.tooltip.pickmode.mouse"),
    ;

    public final String display;
    private final String tooltip;

    PickMode(String in, String tip) {
        this.display = in;
        this.tooltip = tip;
    }

    @Override
    public String toString() {
        return I18n.format(display);
    }

    @Override
    public String getUnlocalized() {
        return tooltip;
    }

    public PickMode next() {
        switch (this) {
            case MOUSE:
                return KEYBOARD;
            default:
                return MOUSE;
        }
    }

    public static int toId(PickMode mode) {
        switch (mode) {
            case MOUSE:
                return 1;
            default:
                return 0;
        }
    }

    public static PickMode fromId(int id) {
        switch (id) {
            case 1:
                return MOUSE;
            default:
                return KEYBOARD;
        }
    }
}
