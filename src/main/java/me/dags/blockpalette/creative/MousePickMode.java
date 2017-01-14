package me.dags.blockpalette.creative;

import me.dags.blockpalette.palette.PaletteMain;
import net.minecraftforge.fml.common.eventhandler.Event;
import org.lwjgl.input.Keyboard;

/**
 * @author dags <dags@dags.me>
 */
public class MousePickMode extends CreativePickMode {

    private boolean showDown = false;
    private boolean lCtrlDown = false;
    private boolean lShiftDown = false;

    public MousePickMode(PaletteMain main) {
        super(main);
    }

    @Override
    void drawScreen(Event event) {
        if (main.getCurrentPalette().isActive()) {
            main.getCurrentPalette().setOverlay(true);
            main.getCurrentPalette().draw(mouseX, mouseY);
            event.setCanceled(true);
        }
    }

    @Override
    void pressMouse(Event event, int button) {
        if (main.getCurrentPalette().isActive()) {
            main.getCurrentPalette().mouseClick(mouseX, mouseY, button);
            event.setCanceled(true);
        } else if (stackUnderMouse != null && button == 0 && !modifierKey()) {
            main.newPalette(stackUnderMouse);
            event.setCanceled(true);
        }
    }

    @Override
    void releaseMouse(Event event, int button) {
        if (main.getCurrentPalette().isActive()) {
            main.getCurrentPalette().mouseRelease(mouseX, mouseY, button);
            event.setCanceled(true);
        }
    }

    @Override
    void pressKey(Event event, int keyCode) {
        if (keyCode == Keyboard.KEY_LCONTROL) {
            lCtrlDown = true;
        } else if (keyCode == Keyboard.KEY_LSHIFT) {
            lShiftDown = true;
        } else if (keyCode == main.show.getKeyCode()) {
            showDown = true;
        }

        if (main.getCurrentPalette().isActive()) {
            event.setCanceled(true);
        }
    }

    @Override
    void releaseKey(Event event, int keyCode) {
        if (keyCode == Keyboard.KEY_LCONTROL) {
            lCtrlDown = false;
        } else if (keyCode == Keyboard.KEY_LSHIFT) {
            lShiftDown = false;
        } else if (keyCode == main.show.getKeyCode()) {
            showDown = false;
        }

        if (main.getCurrentPalette().isActive()) {
            if (keyCode == Keyboard.KEY_ESCAPE || keyCode == main.show.getKeyCode() || main.isInventoryKey(keyCode)) {
                main.getCurrentPalette().onClose();
                main.getCurrentPalette().setInactive();
            }
            event.setCanceled(true);
        }
    }

    private boolean modifierKey() {
        return lCtrlDown || lShiftDown || showDown;
    }
}
