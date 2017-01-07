package me.dags.blockpalette.gui;

import me.dags.blockpalette.palette.PaletteMain;
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

        if (!Keyboard.isKeyDown(main.show.getKeyCode())) {
            Minecraft.getMinecraft().setIngameFocus();
            main.getCurrentPalette().onClose();
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
    public void onGuiClosed() {
        main.getCurrentPalette().onClose();
    }

    @Override
    public void onResize(Minecraft mc, int w, int h) {
        main.getCurrentPalette().onResize(mc, w, h);
    }
}
