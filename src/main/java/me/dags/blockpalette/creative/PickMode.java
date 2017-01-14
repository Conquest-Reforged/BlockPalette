package me.dags.blockpalette.creative;

/**
 * @author dags <dags@dags.me>
 */
public enum PickMode {
    HOT_KEY("Hold HotKey"),
    MOUSE_PRESS("Click Mouse"),
    ;

    public final String display;

    PickMode(String in) {
        this.display = in;
    }

    public PickMode next() {
        switch (this) {
            case MOUSE_PRESS:
                return HOT_KEY;
            default:
                return MOUSE_PRESS;
        }
    }

    public static int toId(PickMode mode) {
        switch (mode) {
            case MOUSE_PRESS:
                return 1;
            default:
                return 0;
        }
    }

    public static PickMode fromId(int id) {
        switch (id) {
            case 1:
                return MOUSE_PRESS;
            default:
                return HOT_KEY;
        }
    }
}
