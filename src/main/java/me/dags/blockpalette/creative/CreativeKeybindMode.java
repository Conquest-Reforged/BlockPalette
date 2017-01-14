package me.dags.blockpalette.creative;

import me.dags.blockpalette.palette.PaletteMain;
import net.minecraftforge.fml.common.eventhandler.Event;

/**
 * @author dags <dags@dags.me>
 */
public class CreativeKeybindMode extends CreativeGUIEvents {

    public CreativeKeybindMode(PaletteMain main) {
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
        if (main.getCurrentPalette().isActive()) {
            event.setCanceled(true);
        } else if (keyCode == main.show.getKeyCode() && stackUnderMouse != null) {
            main.newPalette(stackUnderMouse);
            event.setCanceled(true);
        }
    }

    @Override
    void releaseKey(Event event, int keyCode) {
        if (main.getCurrentPalette().isActive() && keyCode == main.show.getKeyCode()) {
            main.getCurrentPalette().onClose();
            main.getCurrentPalette().setInactive();
            event.setCanceled(true);
        }
    }
}
