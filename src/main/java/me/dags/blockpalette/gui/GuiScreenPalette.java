package me.dags.blockpalette.gui;

import me.dags.blockpalette.creative.PickMode;
import me.dags.blockpalette.palette.PaletteMain;
import me.dags.blockpalette.util.Config;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import org.lwjgl.input.Keyboard;

/**
 * @author dags <dags@dags.me>
 */
public class GuiScreenPalette extends GuiScreen {

    private final PaletteMain main;

    public GuiScreenPalette(PaletteMain main) {
        this.main = main;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float ticks) {
        main.getCurrentPalette().draw(mouseX, mouseY);

        if (Config.pick_mode == PickMode.KEYBOARD && !Keyboard.isKeyDown(main.show.getKeyCode())) {
            Minecraft.getMinecraft().setIngameFocus();
        }
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        main.getCurrentPalette().mouseClick(mouseX, mouseY, mouseButton);
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int mouseButton) {
        main.getCurrentPalette().mouseRelease(mouseX, mouseY, mouseButton);
    }

    @Override
    public void keyTyped(char c, int code) {
        main.getCurrentPalette().keyTyped(c, code);

        if (Config.pick_mode == PickMode.MOUSE && code == main.show.getKeyCode()) {
            Minecraft.getMinecraft().setIngameFocus();
        }
    }

    @Override
    public void onGuiClosed() {
        main.getCurrentPalette().onClose();
    }

    @Override
    public void onResize(Minecraft mc, int w, int h) {
        super.onResize(mc, w, h);
        main.getCurrentPalette().onResize(mc, w, h);
    }
}
