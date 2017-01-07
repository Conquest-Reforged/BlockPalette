package me.dags.blockpalette.gui;

import me.dags.blockpalette.color.ColorF;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

/**
 * @author dags <dags@dags.me>
 */
public class UIVariant extends Gui {

    private static final ResourceLocation WIDGETS = new ResourceLocation("blockpalette", "textures/gui/widgets.png");

    private static final int ITEM_PADDING = 3;
    private static final int SIZE = 22;
    private static final int OFFSET = SIZE / 2;

    private final ItemStack itemStack;
    private final boolean parent;
    private final ColorF color;

    private Area area = Area.empty;
    private int cX = 0;
    private int cY = 0;
    private int xPos = 0;
    private int yPos = 0;

    private float spacing = 0F;
    private boolean hovered = false;
    private boolean visible = false;
    private boolean selected = false;

    private float x1 = 0, y1 = 0;
    private float x2 = 0, y2 = 0;
    private float x3 = 0, y3 = 0;
    private float x4 = 0, y4 = 0;
    private float x5 = 0, y5 = 0;
    private float x6 = 0, y6 = 0;

    public UIVariant(ItemStack itemStack, boolean isParent, ColorF color) {
        this.itemStack = itemStack.copy();
        this.parent = isParent;
        this.visible = true;
        this.color = color;
    }

    public UIVariant(ItemStack itemStack, boolean isParent) {
        this(itemStack, isParent, ColorF.EMPTY);
    }

    public UIVariant(ItemStack itemStack) {
        this(itemStack, false);
    }

    public boolean isParent() {
        return parent;
    }

    public String getDisplayText() {
        return itemStack.getDisplayName();
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    public UIVariant setSelected(boolean selected) {
        this.selected = selected;
        return this;
    }

    public UIVariant setSpacing(float space) {
        this.spacing = space;
        return this;
    }

    public UIVariant setPosition(int x, int y) {
        this.xPos = x - OFFSET;
        this.yPos = y - OFFSET;
        return this;
    }

    public UIVariant setOrigin(int x, int y) {
        this.cX = x;
        this.cY = y;
        if (this.parent) {
            setPosition(x, y);
        }
        return this;
    }

    public boolean isSelected() {
        return selected;
    }

    public boolean isVisible() {
        return visible;
    }

    public boolean mouseOver(int mouseX, int mouseY) {
        return overCenter(mouseX, mouseY) ? parent : area.present() && area.contains(mouseX, mouseY) || overIcon(mouseX, mouseY);
    }

    private boolean overIcon(int mouseX, int mouseY) {
        return mouseX >= xPos && mouseX <= xPos + SIZE && mouseY >= yPos && mouseY <= yPos + SIZE;
    }

    private boolean overCenter(int mouseX, int mouseY) {
        return mouseX >= cX - OFFSET && mouseX <= cX + OFFSET && mouseY >= cY - OFFSET && mouseY <= cY + OFFSET;
    }

    public void updatePosition(int pos, int total, int radius, float progress) {
        visible = progress > (float) pos / (float) total;
        if (!visible) {
            return;
        }

        if (parent) {
            return;
        }

        float rads = calcRadiansAtIndex(pos, progress);
        int xPos = cX + Math.round(radius * (float) Math.cos(rads));
        int yPos = cY + Math.round(radius * (float) Math.sin(rads));

        setPosition(xPos, yPos);

        float halfSpacing = spacing / 2F;

        // Calc the min and max points about this items.
        // Combine with the origin to create a triangle used to determine whether the mouse is hovered over or not
        float minRad = rads - halfSpacing;

        float xMin = cX + (radius + SIZE) * (float) Math.cos(minRad);
        float yMin = cY + (radius + SIZE) * (float) Math.sin(minRad);

        float maxRad = rads + halfSpacing;
        float xMax = cX + (radius + SIZE) * (float) Math.cos(maxRad);
        float yMax = cY + (radius + SIZE) * (float) Math.sin(maxRad);

        area = new Area(xMin, yMin, xMax, yMax, cX, cY);


        float dx = xPos - cX, dy = yPos - cY;
        float dx1 = xMin - cX, dy1 = yMin - cY;
        float dx2 = xMax - cX, dy2 = yMax - cY;

        // Bottom left
        x1 = cX + (0.5F * dx1);
        y1 = cY + (0.5F * dy1);

        // Bottom right
        x2 = cX + (0.5F * dx2);
        y2 = cY + (0.5F * dy2);

        // Bottom middle
        x3 = xPos - (0.5F * dx);
        y3 = yPos - (0.5F * dy);

        // Top middle
        x4 = xPos + ((SIZE / (float) radius) * dx);
        y4 = yPos + ((SIZE / (float) radius) * dy);

        // Top left
        x5 = xMin;
        y5 = yMin;

        // Top right
        x6 = xMax;
        y6 = yMax;
    }

    private float calcRadiansAtIndex(int index, float modifier) {
        // Starting position at twelve o'clock
        float start = 1.5F * (float) Math.PI;

        // Calc amount of rotation (from start) depending on the index of the items
        float rotation = index * spacing;

        // Add rotation to the start value
        // Multiplication of modifier here creates the spinning animation as the modifier increases to 1.0
        return (modifier * start) + rotation;
    }

    public void draw(int mouseX, int mouseY, boolean showSegments) {
        this.hovered = visible;

        if (visible) {
            this.hovered = mouseOver(mouseX, mouseY);

            // Draw the segment
            if (showSegments && !this.parent) {
                GlStateManager.enableBlend();
                GlStateManager.disableTexture2D();

                float highlight = (hovered || selected) ? 0.1F : 0F;

                Tessellator tessellator = Tessellator.getInstance();
                VertexBuffer vertexbuffer = tessellator.getBuffer();

                // Fill the shape
                GlStateManager.color(color.red + highlight, color.green + highlight, color.blue + highlight, 0.75F);
                vertexbuffer.begin(GL11.GL_POLYGON, DefaultVertexFormats.POSITION);
                vertexbuffer.pos(x3, y3, 0F).endVertex();
                vertexbuffer.pos(x2, y2, 0F).endVertex();
                vertexbuffer.pos(x6, y6, 0F).endVertex();
                vertexbuffer.pos(x4, y4, 0F).endVertex();
                vertexbuffer.pos(x5, y5, 0F).endVertex();
                vertexbuffer.pos(x1, y1, 0F).endVertex();
                tessellator.draw();

                // Draw the outline
                GlStateManager.glLineWidth(3F);
                GlStateManager.color(0F + highlight, 0F + highlight, 0F + highlight, 0.15F);
                vertexbuffer.begin(GL11.GL_LINE_LOOP, DefaultVertexFormats.POSITION);
                vertexbuffer.pos(x3, y3, 0F).endVertex();
                vertexbuffer.pos(x2, y2, 0F).endVertex();
                vertexbuffer.pos(x6, y6, 0F).endVertex();
                vertexbuffer.pos(x4, y4, 0F).endVertex();
                vertexbuffer.pos(x5, y5, 0F).endVertex();
                vertexbuffer.pos(x1, y1, 0F).endVertex();
                tessellator.draw();

                GlStateManager.disableBlend();
                GlStateManager.enableTexture2D();
            }

            // Draw the slot
            GlStateManager.enableLighting();
            GlStateManager.enableBlend();
            Minecraft.getMinecraft().getTextureManager().bindTexture(WIDGETS);
            Gui.drawModalRectWithCustomSizedTexture(xPos, yPos, 0, 0, 22, 22, 66, 22);
            GlStateManager.disableLighting();
            GlStateManager.disableBlend();

            // Draw the item
            RenderHelper.enableGUIStandardItemLighting();
            Minecraft.getMinecraft().getRenderItem().renderItemAndEffectIntoGUI(itemStack, xPos + ITEM_PADDING, yPos + ITEM_PADDING);
            RenderHelper.disableStandardItemLighting();
        }
    }

    public void drawHighlighted() {
        // Draw the slot overlay
        GlStateManager.enableBlend();
        Minecraft.getMinecraft().getTextureManager().bindTexture(WIDGETS);
        Gui.drawModalRectWithCustomSizedTexture(xPos, yPos, 44, 0, 22, 22, 66, 22);
        GlStateManager.disableBlend();
    }

    public void drawSelected() {
        // Draw the 'selected' overlay
        GlStateManager.enableAlpha();
        Minecraft.getMinecraft().getTextureManager().bindTexture(WIDGETS);
        Gui.drawModalRectWithCustomSizedTexture(xPos, yPos, 22, 0, 22, 22, 66, 22);
        GlStateManager.disableAlpha();
    }

    private static class Area {

        private static final Area empty = new Area(0F, 0F, 1F, 1F, 2F, 2F);

        private final float x3, y3;
        private final float y23, x32, y31, x13;
        private final float det, minD, maxD;

        private Area(float x1, float y1, float x2, float y2, float x3, float y3) {
            this.x3 = x3;
            this.y3 = y3;
            y23 = y2 - y3;
            x32 = x3 - x2;
            y31 = y3 - y1;
            x13 = x1 - x3;
            det = y23 * x13 - x32 * y31;
            minD = Math.min(det, 0);
            maxD = Math.max(det, 0);
        }

        boolean present() {
            return this != empty;
        }

        boolean contains(float x, float y) {
            float dx = x - x3;
            float dy = y - y3;
            float a = y23 * dx + x32 * dy;
            if (a < minD || a > maxD) {
                return false;
            }

            float b = y31 * dx + x13 * dy;
            if (b < minD || b > maxD) {
                return false;
            }

            float c = det - a - b;
            return !(c < minD || c > maxD);
        }
    }
}
