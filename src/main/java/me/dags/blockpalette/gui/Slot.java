package me.dags.blockpalette.gui;

import me.dags.blockpalette.palette.PaletteItem;
import me.dags.blockpalette.util.Config;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
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

    Slot(PaletteItem item) {
        this.item = item;

        if (item == null) {
            throw new NullPointerException("!!!!!!!!!!!!!!!!!!!!!!");
        }
    }

    int xPos() {
        return xPos;
    }

    int yPos() {
        return yPos;
    }

    boolean isSelected() {
        return selected;
    }

    boolean isEmpty() {
        return item.isEmpty();
    }

    public PaletteItem getItem() {
        return item;
    }

    ItemStack getStack() {
        return item.isEmpty() ? null : item.getItemStack();
    }

    boolean mouseOver(int x, int y) {
        return x >= xPos - 11 && x <= xPos + 11 && y >= yPos - 11 && y <= yPos + 11;
    }

    void setBounds(SlotBounds bounds) {
        this.bounds = bounds;
    }

    void setHighlight(int color, int invert, float size) {
        this.highlightColor = color;
        this.highlightSize = size;
        this.invertColor = invert;
    }

    void setPosition(int x, int y) {
        this.xPos = x;
        this.yPos = y;
    }

    void setScale(float f) {
        this.scale = f;
    }

    void setHovered(boolean hovered) {
        this.hovered = hovered;
    }

    void setSelected(boolean selected) {
        this.selected = selected;
    }

    public void draw() {
        if (!isEmpty()) {
            GlStateManager.pushMatrix();
            GlStateManager.enableDepth();
            GlStateManager.translate(xPos, yPos, 0);
            GlStateManager.scale(scale, scale, scale);

            GlStateManager.enableBlend();
            Minecraft.getMinecraft().getTextureManager().bindTexture(SLOT);
            GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
            Gui.drawModalRectWithCustomSizedTexture(-11, -11, 0, 0, 22, 22, 22, 22);
            GlStateManager.disableBlend();

            if (hovered || selected) {
                int color = selected ? invertColor : highlightColor;
                Slot.drawHighlightedItem(item, 0, 0, highlightSize, color);
            } else {
                Slot.drawItem(item, -8, -8);
            }

            GlStateManager.disableDepth();
            GlStateManager.popMatrix();
        }
    }

    public void drawBounds() {
        if (!isEmpty() && !Config.match_textures && Config.color_opacity > 0) {
            bounds.draw(item.getColor().red, item.getColor().green, item.getColor().blue, 1F);
        }
    }

    void drawDisplayString(int cx, int yPos) {
        if (!item.isEmpty()) {
            FontRenderer renderer = Minecraft.getMinecraft().fontRendererObj;
            String text = item.getItemStack().getDisplayName();
            int length = renderer.getStringWidth(text);
            int half = length / 2;
            renderer.drawStringWithShadow(text, cx - half, yPos, 0xFFFFFF);
        }
    }

    static void drawItem(PaletteItem item, int x, int y) {
        if (!item.isEmpty()) {
            Slot.drawStack(item.getItemStack(), x, y);
        }
    }

    static void drawHighlightedItem(PaletteItem item, int x, int y, float scale, int color) {
        if (!item.isEmpty()) {
            Slot.drawHighlightedStack(item.getItemStack(), x, y, scale, color);
        }
    }

    static void drawStack(ItemStack stack, int x, int y) {
        if (stack != null) {
            RenderHelper.enableGUIStandardItemLighting();
            Minecraft.getMinecraft().getRenderItem().renderItemIntoGUI(stack, x, y);
        }
    }

    static void drawHighlightedStack(ItemStack stack, int x, int y, float scale, int color) {
        if (stack != null) {
            float rescale0 = 1 / scale;
            GlStateManager.translate(x, y, 10F);
            GlStateManager.scale(scale, scale, scale);
            GlStateManager.enableOutlineMode(color);
            Minecraft.getMinecraft().getRenderItem().renderItemIntoGUI(stack, -8, -8);
            GlStateManager.disableOutlineMode();
            GlStateManager.scale(rescale0, rescale0, rescale0);
            GlStateManager.translate(x, y, 100F);

            RenderHelper.enableGUIStandardItemLighting();
            Minecraft.getMinecraft().getRenderItem().renderItemIntoGUI(stack, -8, -8);
            GlStateManager.disableDepth();
        }
    }
}
