package me.dags.blockpalette.creative;

import me.dags.blockpalette.gui.PaletteScreen;
import me.dags.blockpalette.palette.PaletteMain;
import net.minecraftforge.fml.common.eventhandler.Event;

import java.io.IOException;

/**
 * @author dags <dags@dags.me>
 */
public class KeybindPickMode extends CreativePickMode {

    private PaletteScreen screen;

    public KeybindPickMode(PaletteMain main) {
        super(main);
        this.screen = new PaletteScreen(main);
        this.screen.initGui();
        this.main.newPalette(null);
    }

    @Override
    void drawScreen(Event event) {
        if (main.getPalette().isPresent()) {
            screen.drawScreen(mouseX, mouseY, 0F);
            event.setCanceled(true);
        }
    }

    @Override
    void pressMouse(Event event, int button) {
        if (main.getPalette().isPresent()) {
            try {
                screen.mouseClicked(mouseX, mouseY, button);
                event.setCanceled(true);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    void releaseMouse(Event event, int button) {
        if (main.getPalette().isPresent()) {
            screen.mouseReleased(mouseX, mouseY, button);
            event.setCanceled(true);
        }
    }

    @Override
    void pressKey(Event event, int keyCode) {
        if (main.getPalette().isPresent()) {
            event.setCanceled(true);
        } else if (keyCode == main.show.getKeyCode() && stackUnderMouse != null) {
            main.newPalette(stackUnderMouse);
            screen = new PaletteScreen(main);
            screen.setCreativeOverlay(true);
            screen.initGui();
            event.setCanceled(true);
        }
    }

    @Override
    void releaseKey(Event event, int keyCode) {
        if (main.getPalette().isPresent() && keyCode == main.show.getKeyCode()) {
            screen.onGuiClosed();
            event.setCanceled(true);
        }
    }
}
