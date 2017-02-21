package me.dags.blockpalette.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

/**
 * @author dags <dags@dags.me>
 */
public class Render {

    public static void cleanup() {
        GlStateManager.depthMask(true);
        GlStateManager.enableDepth();
        GlStateManager.enableAlpha();
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        GlStateManager.clearColor(1.0f, 1.0f, 1.0f, 1.0f);
    }

    public static void beginMask(ResourceLocation texture, int left, int top, int width, int height, float u, float v, float umax, float vmax) {
        GlStateManager.color(1, 1, 1, 1);
        GlStateManager.enableTexture2D();
        GlStateManager.enableDepth();
        GlStateManager.depthMask(true);
        GlStateManager.depthFunc(GL11.GL_ALWAYS);
        GlStateManager.colorMask(false, false, false, true);
        GlStateManager.blendFunc(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.SRC_ALPHA);

        GlStateManager.pushMatrix();
        GlStateManager.translate(0, 0, 1);
        drawTexture(texture, left, top, width, height, u, v, umax, vmax);
        GlStateManager.popMatrix();

        GlStateManager.disableTexture2D();
        GlStateManager.depthMask(false);
        GlStateManager.depthFunc(GL11.GL_GREATER);
        GlStateManager.colorMask(true, true, true, false);
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.DST_ALPHA);
    }

    public static void endMask() {
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        GlStateManager.colorMask(true, true, true, true);
        GlStateManager.depthMask(true);
        GlStateManager.depthFunc(GL11.GL_LEQUAL);
        GlStateManager.disableDepth();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
    }

    public static void beginItems() {
        GlStateManager.clear(GL11.GL_DEPTH_BUFFER_BIT);
        GlStateManager.enableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableDepth();
        GlStateManager.depthMask(true);
        GlStateManager.depthFunc(GL11.GL_LEQUAL);
        RenderHelper.enableGUIStandardItemLighting();

        GlStateManager.pushMatrix();
        GlStateManager.translate(0, 0, 100);
    }

    public static void endItems() {
        GlStateManager.popMatrix();

        GlStateManager.disableDepth();
        RenderHelper.disableStandardItemLighting();
        GlStateManager.clearColor(1, 1, 1, 1);
    }

    public static void beginSlot(int xPos, int yPos, float scale) {
        GlStateManager.pushMatrix();
        GlStateManager.translate(xPos, yPos, 1);
        GlStateManager.scale(scale, scale, scale);
        GlStateManager.alphaFunc(GL11.GL_NOTEQUAL, 0);
    }

    public static void endSlot() {
        GlStateManager.popMatrix();
    }

    public static void drawTexture(ResourceLocation texture, int left, int top, int width, int height, float u, float v, float umax, float vwidth) {
        Minecraft.getMinecraft().getTextureManager().bindTexture(texture);
        Gui.drawModalRectWithCustomSizedTexture(left, top, u, v, width, height, umax, vwidth);
    }

    public static void drawItemStack(ItemStack stack, int x, int y) {
        if (stack == null) {
            return;
        }

        Minecraft.getMinecraft().getRenderItem().renderItemIntoGUI(stack, x, y);
    }

    public static void drawOverlays(ItemStack stack, int x, int y) {
        Minecraft.getMinecraft().getRenderItem().renderItemOverlays(Minecraft.getMinecraft().fontRendererObj, stack, x, y);
    }

    public static void drawHighlightedItemStack(ItemStack stack, int x, int y, float scale, int color) {
        if (stack == null) {
            return;
        }

        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, 100);
        GlStateManager.scale(scale, scale, scale);
        GlStateManager.enableOutlineMode(color);
        Minecraft.getMinecraft().getRenderItem().renderItemIntoGUI(stack, -8, -8);
        GlStateManager.disableOutlineMode();
        GlStateManager.popMatrix();

        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, 200);
        Minecraft.getMinecraft().getRenderItem().renderItemIntoGUI(stack, -8, -8);
        GlStateManager.popMatrix();
    }
}
