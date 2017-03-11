package me.dags.blockpalette.creative;

import net.minecraft.client.resources.I18n;

/**
 * @author dags <dags@dags.me>
 */
public enum PickMode {
    KEYBOARD("palette.pickmode.keyboard"),
    MOUSE("palette.pickmode.mouse"),
    ;

    public final String display;

    PickMode(String in) {
        this.display = in;
    }

    @Override
    public String toString() {
        return I18n.format(display);
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
