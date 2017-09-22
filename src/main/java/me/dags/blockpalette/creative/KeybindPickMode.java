package me.dags.blockpalette.creative;

import me.dags.blockpalette.palette.PaletteMain;
import me.dags.blockpalette.palette.PaletteScreen;
import me.dags.blockpalette.util.Config;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;

import java.io.IOException;

/**
 * @author dags <dags@dags.me>
 */
@SideOnly(Side.CLIENT)
public class KeybindPickMode extends CreativePickMode {

    private PaletteScreen screen;

    public KeybindPickMode(PaletteMain main) {
        super(main);
        this.screen = new PaletteScreen(main);
        this.screen.initGui();
        this.main.newCreativePalette(null);
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
            if (button - 100 == main.show.getKeyCode()) {
                screen.onGuiClosed();
                event.setCanceled(true);
            } else {
                try {
                    screen.mouseClicked(mouseX, mouseY, button);
                    event.setCanceled(true);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else if (button - 100 == main.show.getKeyCode() && stackUnderMouse != null) {
            main.newCreativePalette(stackUnderMouse);
            screen = new PaletteScreen(main);
            screen.setCreativeOverlay(true);
            screen.initGui();
            event.setCanceled(true);
        }
    }

    @Override
    void releaseMouse(Event event, int button) {
        if (main.getPalette().isPresent()) {
            if (button - 100 == main.show.getKeyCode()) {
                if (Config.hold_key && button - 100 == main.show.getKeyCode()) {
                    screen.onGuiClosed();
                    event.setCanceled(true);
                }
            } else {
                screen.mouseReleased(mouseX, mouseY, button);
                event.setCanceled(true);
            }
        }
    }

    @Override
    void pressKey(Event event, char c, int keyCode) {
        if (main.getPalette().isPresent()) {
            event.setCanceled(true);
            if (Config.pick_mode == PickMode.KEYBOARD && !Config.hold_key) {
                if (keyCode == Keyboard.KEY_ESCAPE || keyCode == main.show.getKeyCode() || main.isInventoryKey(keyCode)) {
                    screen.onGuiClosed();
                    main.newCreativePalette(null);
                } else {
                    try {
                        screen.keyTyped(c, keyCode);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        } else if (keyCode == main.show.getKeyCode() && stackUnderMouse != null) {
            main.newCreativePalette(stackUnderMouse);
            screen = new PaletteScreen(main);
            screen.setCreativeOverlay(true);
            screen.initGui();
            event.setCanceled(true);
        }
    }

    @Override
    void releaseKey(Event event, char c, int keyCode) {
        if (main.getPalette().isPresent()) {
            if (Config.hold_key) {
                if (keyCode == main.show.getKeyCode()) {
                    screen.onGuiClosed();
                    event.setCanceled(true);
                }
            }
        }
    }
}
