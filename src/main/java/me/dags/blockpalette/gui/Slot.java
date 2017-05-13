package me.dags.blockpalette.gui;

import me.dags.blockpalette.palette.PaletteItem;
import me.dags.blockpalette.util.Config;
import me.dags.blockpalette.util.Render;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

/**
 * @author dags <dags@dags.me>
 */
public class Slot {

    private static final ResourceLocation SLOT = new ResourceLocation("blockpalette", "textures/gui/slot.png");

    private final PaletteItem item;

    private boolean hovered = false;
    private boolean selected = false;
    private float scale = 1F;
    private int xPos = 0;
    private int yPos = 0;
    private int invertColor = 0;
    private int highlightColor = 0;
    private float highlightSize = 1.1F;

    private SlotBounds bounds = new SlotBounds();

    public Slot(PaletteItem item) {
        this.item = item;
    }

    public int xPos() {
        return xPos;
    }

    public int yPos() {
        return yPos;
    }

    public boolean isSelected() {
        return selected;
    }

    public boolean isEmpty() {
        return item.isEmpty();
    }

    public PaletteItem getItem() {
        return item;
    }

    public ItemStack getStack() {
        return item.isEmpty() ? null : item.getItemStack();
    }

    public boolean mouseOver(int x, int y) {
        return x >= xPos - 11 && x <= xPos + 11 && y >= yPos - 11 && y <= yPos + 11;
    }

    public void setBounds(SlotBounds bounds) {
        this.bounds = bounds;
    }

    public void setHighlight(int color, int invert, float size) {
        this.highlightColor = color;
        this.highlightSize = size;
        this.invertColor = invert;
    }

    public void setPosition(int x, int y) {
        this.xPos = x;
        this.yPos = y;
    }

    public void setScale(float f) {
        this.scale = f;
    }

    public void setHovered(boolean hovered) {
        this.hovered = hovered;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public void draw() {
        if (!isEmpty()) {
            Render.beginSlot(xPos, yPos, scale);
            Render.drawTexture(SLOT, -11, -11, 22, 22, 0, 0, 22, 22);
            if (hovered || selected) {
                int color = selected ? invertColor : highlightColor;
                Render.drawHighlightedItemStack(item.getItemStack(), 0, 0, highlightSize, color);
            } else {
                Render.drawItemStack(item.getItemStack(), -8, -8);
            }
            Render.endSlot();
        }
    }

    public void drawBounds() {
        if (!isEmpty() && !Config.match_textures) {
            bounds.draw(item.getColor().red, item.getColor().green, item.getColor().blue, 1F);
        }
    }
}
