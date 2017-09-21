package me.dags.blockpalette.gui;

import me.dags.blockpalette.util.Value;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.List;

/**
 * @author dags <dags@dags.me>
 */
@SideOnly(Side.CLIENT)
public class Settings implements Positional {

    private final List<GuiButton> buttons = new ArrayList<>();
    private final List<Tooltip> tooltips = new ArrayList<>();
    private boolean centreVertically = true;
    private boolean centerHorizontally = true;
    private int buttonMaxWidth = 250;
    private int buttonMinWidth = 100;
    private int buttonHeight = 20;
    private int buttonSeparator = 1;
    private int padding = 20;

    private int left = 0;
    private int top = 0;
    private int width = 0;
    private int height = 0;

    private Animation open;
    private Animation close;
    private Animation animation;

    public int getWidth() {
        return width;
    }

    public boolean onScreen(int width, int height) {
        return left >= 0 && left < width && top >= 0 && top < height;
    }

    public void setAnimations(Animation open, Animation close) {
        this.open = open;
        this.close = close;
    }

    public void open() {
        open.reset();
        animation = open;
    }

    public void close() {
        close.reset();
        animation = close;
    }

    public void add(GuiButton button) {
        this.buttons.add(button);
    }

    public void add(GuiButton button, String tooltip) {
        this.buttons.add(button);
        this.tooltips.add(Tooltip.of(button, new Tooltip.Simple(tooltip)));
    }

    public void add(GuiButton button, Value<? extends Tooltip.Provider> value) {
        this.buttons.add(button);
        this.tooltips.add(Tooltip.of(button, new Tooltip.PointerTip<>(value)));
    }

    public void setCentreVertically(boolean centreVertically) {
        this.centreVertically = centreVertically;
    }

    @Override
    public void setPosition(int left, int top) {
        this.left = left;
        this.top = top;
    }

    public void setSize(int width, int height) {
        this.width = Math.max(width, buttonMinWidth + padding + padding);
        this.height = height;
    }

    public void mousePressed(Minecraft minecraft, int mouseX, int mouseY) {
        for (GuiButton button : buttons) {
            button.mousePressed(minecraft, mouseX, mouseY);
        }
    }

    public void mouseReleased(int mouseX, int mouseY) {
        for (GuiButton button : buttons) {
            button.mouseReleased(mouseX, mouseY);
        }
    }

    public void draw(Minecraft minecraft, int mouseX, int mouseY, float ticks) {
        if (animation != null && !animation.isComplete()) {
            animation.tick();
        }

        int buttonWidth = width - padding - padding;
        int left = this.left + padding;
        int top = this.top + padding;

        if (buttonWidth > buttonMaxWidth) {
            int pad = (buttonWidth - buttonMaxWidth) / 2;
            buttonWidth = buttonMaxWidth;
            if (centerHorizontally) {
                left += pad;
            }
        } else if (buttonWidth < buttonMinWidth) {
            int pad = buttonMinWidth - buttonWidth;
            buttonWidth = buttonMinWidth - 2;
            pad = pad < (2 * padding) ? pad / 2 : padding;
            left = left - pad + 1;
        }

        if (centreVertically) {
            int totalHeight = buttons.size() * (buttonHeight + buttonSeparator);
            int dy = height - totalHeight;
            top = dy / 2;
        }

        drawBackground();

        for (GuiButton button : buttons) {
            button.yPosition = top;
            button.xPosition = left;
            button.width = buttonWidth;
            button.height = buttonHeight;
            button.drawButton(minecraft, mouseX, mouseY);
            top += buttonHeight + buttonSeparator;
        }
    }

    public void drawTooltips(int mouseX, int mouseY, float ticks) {
        for (Tooltip tooltip : tooltips) {
            tooltip.draw(mouseX, mouseY, ticks);
        }
    }

    public void drawBackground() {
        Gui.drawRect(left, top, left + width, top + height, 0x20222222);
        //Gui.drawRect(left - 2, top, left, top + height, 0x22FFFFFF);
        //Gui.drawRect(left + width, top, left + width + 2, top + height, 0x22FFFFFF);
    }
}
