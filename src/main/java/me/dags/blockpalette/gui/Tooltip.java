package me.dags.blockpalette.gui;

import me.dags.blockpalette.util.Pointer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.resources.I18n;

/**
 * @author dags <dags@dags.me>
 */
public class Tooltip {

    private final FontRenderer fontRenderer = Minecraft.getMinecraft().fontRendererObj;
    private final Provider tooltip;
    private final int width;
    private final int pad = 2;
    private final int offsetX;
    private final GuiButton button;

    private Tooltip(GuiButton button, Provider tooltip, int width) {
        this.button = button;
        this.tooltip = tooltip;
        this.width = width;
        this.offsetX = width / 2;
    }

    public void draw(int x, int y) {
        if (button.isMouseOver()) {
            String message = I18n.format(tooltip.getUnlocalized());

            int stringWidth = fontRenderer.getStringWidth(message);
            int height = 10 * (stringWidth / width);
            int offsetY = height + pad;

            int left = x - offsetX - pad;
            int top = y - offsetY - pad;

            int right = left + width + pad + pad;
            int bottom = top + height + pad + pad;
            Gui.drawRect(left, top, right, bottom, 0x33000000);

            int textLeft = left + pad;
            int textTop = top + pad;
            fontRenderer.drawSplitString(message, textLeft, textTop, width, 0xFFFFFFFF);
        }
    }

    public static Tooltip of(GuiButton button, Provider provider) {
        return new Tooltip(button, provider, 150);
    }

    public interface Provider {

        String getUnlocalized();
    }

    static class Simple implements Provider {

        private final String tooltip;

        Simple(String tooltip) {
            this.tooltip = tooltip;
        }

        @Override
        public String getUnlocalized() {
            return tooltip;
        }
    }

    static class PointerTip<T extends Provider> implements Provider {

        private final Pointer<T> option;

        PointerTip(Pointer<T> option) {
            this.option = option;
        }

        @Override
        public String getUnlocalized() {
            return option.get().getUnlocalized();
        }
    }
}
