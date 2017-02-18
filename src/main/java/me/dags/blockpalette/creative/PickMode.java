package me.dags.blockpalette.creative;

/**
 * @author dags <dags@dags.me>
 */
public enum PickMode {
    KEYBOARD("Keyboard"),
    MOUSE("Mouse"),
    ;

    public final String display;

    PickMode(String in) {
        this.display = in;
    }

    @Override
    public String toString() {
        return display;
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
