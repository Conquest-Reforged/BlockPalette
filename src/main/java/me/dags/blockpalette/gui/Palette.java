package me.dags.blockpalette.gui;

import me.dags.blockpalette.color.ColorF;
import me.dags.blockpalette.palette.PaletteItem;
import me.dags.blockpalette.util.Config;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.inventory.CreativeCrafting;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author dags <dags@dags.me>
 */
public class Palette {

    public static final Palette EMPTY = new Palette(new Slot(PaletteItem.EMPTY), Collections.<Slot>emptyList(), 0F);

    private static final ResourceLocation WHEEL = new ResourceLocation("blockpalette", "textures/gui/wheel.png");
    private static final ResourceLocation MASK0 = new ResourceLocation("blockpalette", "textures/gui/wheel_mask0.png");
    private static final ResourceLocation MASK1 = new ResourceLocation("blockpalette", "textures/gui/wheel_mask1.png");

    private static final int EDGES = 6; // number of sides on polygon
    private static final int SCALE_THRESHOLD = 18; // number of filled slots before scaling down items
    private static final float CENTER_SCALE = 1.75F; // scale of item in center

    private final List<Slot> allSlots = new ArrayList<>();
    private final List<Slot> slots = new ArrayList<>();
    private final int radius = 75;
    private final float scale;
    private final Slot center;

    private int selectedColor = 0xFFFFFF;
    private int highlightColor = 0xFFFFFF;
    private float highlightRadius = 1.1F;

    private int centerX = 0;
    private int centerY = 0;

    private Slot underMouse = null;
    private Slot selected = null;

    private Palette(Slot center, List<Slot> slots, float scale) {
        this.center = center;
        this.slots.addAll(slots);
        this.allSlots.add(center);
        this.allSlots.addAll(slots);
        this.scale = scale;
    }

    public boolean isPresent() {
        return this != EMPTY;
    }

    public ItemStack getCenter() {
        return center.getStack();
    }

    public Slot getUnderMouse() {
        return underMouse;
    }

    public int getWidth() {
        return radius + 40;
    }

    public void setSelected(Slot slot) {
        this.selected = slot;
    }

    public void setHighlightColor(int r, int g, int b) {
        this.highlightColor = ColorF.rgb(r, g, b);
        this.selectedColor = ColorF.rgb(Math.max(0, r - 45), Math.max(0, g - 45), Math.max(0, b - 45));
    }

    public void setHighlightRadius(float highlightRadius) {
        this.highlightRadius = highlightRadius;
    }

    public void drawScreen(int mouseX, int mouseY) {
        if (!isPresent()) {
            return;
        }

        handleMouse(mouseX, mouseY);

        int rad = radius + 44;
        int dim = (radius + 44) * 2;


        GlStateManager.color(1F, 1F, 1F, 1F);
        Minecraft.getMinecraft().getTextureManager().bindTexture(WHEEL);
        Gui.drawModalRectWithCustomSizedTexture(centerX - rad, centerY - rad, 0, 0, dim, dim, dim , dim);

        if (!Config.match_textures) {
            GlStateManager.pushMatrix();
            drawMask(MASK0, rad, dim);
            GlStateManager.disableTexture2D();
            for (Slot slot : slots) {
                slot.drawBounds();
            }
            GlStateManager.enableTexture2D();
            GlStateManager.popMatrix();

            GlStateManager.pushMatrix();
            drawMask(MASK1, rad, dim);
            GlStateManager.disableTexture2D();
            center.drawBounds();
            GlStateManager.enableTexture2D();
            GlStateManager.popMatrix();
        }

        for (Slot slot : allSlots) {
            slot.setHighlight(highlightColor, selectedColor, highlightRadius);
            slot.draw();
        }

        Slot display = underMouse != null ? underMouse : selected;
        if (display != null) {
            display.drawDisplayString(centerX, centerY + radius + 25);
        }

        GlStateManager.disableAlpha();
        GlStateManager.disableBlend();
    }

    private void drawMask(ResourceLocation location, int radius, int dim) {
        GlStateManager.disableAlpha();
        GlStateManager.enableBlend();

        GlStateManager.colorMask(false, false, false, true);
        GlStateManager.color(1F, 1F, 1F, Config.color_opacity);
        GlStateManager.blendFunc(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);

        Minecraft.getMinecraft().getTextureManager().bindTexture(location);
        Gui.drawModalRectWithCustomSizedTexture(centerX - radius, centerY - radius, 0, 0, dim, dim, dim, dim);

        GlStateManager.colorMask(true, true, true, true);
        GlStateManager.blendFunc(GlStateManager.SourceFactor.DST_ALPHA, GlStateManager.DestFactor.ONE_MINUS_DST_ALPHA);
    }

    private void handleMouse(int mouseX, int mouseY) {
        underMouse = null;

        float radsq = radius * radius;
        float distance = radsq;

        for (Slot slot : allSlots) {
            int dx = mouseX - slot.xPos();
            int dy = mouseY - slot.yPos();
            float dsq = (dx * dx + dy * dy);
            float ds = (radsq - dsq) / radsq;
            float scale = this.scale;

            if (ds > 0.5) {
                scale += Math.min(Math.max(ds * ds * 1.05F, 0), 2F);
            }

            slot.setHovered(false);
            slot.setScale(slot == center ? CENTER_SCALE : scale);

            if (!slot.isEmpty() && slot.mouseOver(mouseX, mouseY) && dsq < distance) {
                underMouse = slot;
                distance = dsq;
            }
        }

        if (underMouse != null) {
            underMouse.setHovered(true);
        }
    }

    public void resize(int width, int height) {
        if (!isPresent()) {
            return;
        }

        this.centerX = width / 2;
        this.centerY = height / 2;
        this.center.setPosition(centerX, centerY);
        this.center.setScale(CENTER_SCALE);

        me.dags.blockpalette.shape.Polygon polygon = new me.dags.blockpalette.shape.Polygon(EDGES, radius, 40, 40);
        polygon.init(centerX, centerY);

        float spacing = 360F / (float) slots.size();
        float halfSpacing = spacing / 2F;

        for (int i = 0; i < slots.size(); i++) {
            float angle = clampAngle((i * spacing) - 90);
            Point position = polygon.getPosition(angle);
            SlotBounds bounds = polygon.getBounds(angle, halfSpacing);

            Slot slot = slots.get(i);
            slot.setPosition(position.x, position.y);
            slot.setBounds(bounds);
        }

        center.setBounds(innerBounds(centerX, centerY));
    }

    public void close() {
        if (!isPresent()) {
            return;
        }

        EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
        CreativeCrafting crafting = new CreativeCrafting(Minecraft.getMinecraft());
        player.inventoryContainer.addListener(crafting);

        if (player.capabilities.isCreativeMode) {
            for (Slot slot : allSlots) {
                if (!slot.isEmpty() && slot.isSelected()) {
                    player.inventory.addItemStackToInventory(slot.getStack());
                }
            }

            player.inventoryContainer.detectAndSendChanges();
        }

        player.inventoryContainer.removeListener(crafting);
    }

    private static float clampAngle(float value) {
        return value < 0F ? 360F + value : value > 360F ? value - 360F : value;
    }

    private static SlotBounds innerBounds(int centerX, int centerY) {
        me.dags.blockpalette.shape.Polygon polygon = new me.dags.blockpalette.shape.Polygon(6, 40, 1, 1);
        polygon.init(centerX, centerY);
        return polygon.outline();
    }

    public static Palette texturePalette(PaletteItem centerItem, List<PaletteItem> items) {
        int count = Math.max(SCALE_THRESHOLD, items.size());
        float scale = 1.15F;
        if (count > SCALE_THRESHOLD) {
            float dif = (count - SCALE_THRESHOLD) / (float) SCALE_THRESHOLD;
            scale -= Math.min(dif, 0.45F);
        }

        List<Slot> slots = new ArrayList<>();
        Slot center = new Slot(centerItem);

        for (int i = 0; i < count; i++) {
            PaletteItem stack = i < items.size() ? items.get(i) : PaletteItem.EMPTY;
            Slot slot = new Slot(stack);
            slots.add(slot);
        }

        return new Palette(center, slots, scale);
    }

    public static Palette colorPalette(PaletteItem centerItem, List<PaletteItem> items) {
        int count = items.size();
        float scale = 1.15F;
        if (count > SCALE_THRESHOLD) {
            float dif = (count - SCALE_THRESHOLD) / (float) SCALE_THRESHOLD;
            scale -= Math.min(dif, 0.45F);
        }

        List<Slot> slots = new ArrayList<>();
        Slot center = new Slot(centerItem);

        for (int i = 0; i < count; i++) {
            PaletteItem stack = i < items.size() ? items.get(i) : PaletteItem.EMPTY;
            Slot slot = new Slot(stack);
            slots.add(slot);
        }

        return new Palette(center, slots, scale);
    }
}
