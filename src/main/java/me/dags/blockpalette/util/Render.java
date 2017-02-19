package me.dags.blockpalette.util;

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
public class Render {

    public static void cleanup() {
        GlStateManager.depthMask(true);
        GL11.glClear(256);
        GlStateManager.clear(256);
        GlStateManager.enableDepth();
        GlStateManager.depthFunc( 515);
        GlStateManager.enableAlpha();
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        GlStateManager.clearColor(1.0f, 1.0f, 1.0f, 1.0f);
    }

    public static void beginMask(ResourceLocation texture, int left, int top, int width, int height, float u, float v, float umax, float vmax) {
        cleanup();

        GlStateManager.enableAlpha();
        GlStateManager.enableBlend();

        GlStateManager.enableTexture2D();
        Minecraft.getMinecraft().getTextureManager().bindTexture(texture);
        GlStateManager.colorMask(false, false, false, true);
        GlStateManager.alphaFunc(GL11.GL_GREATER, 0F);
        GlStateManager.blendFunc(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        drawTexture(texture, left, top, width, height, u, v, umax, vmax);

        GlStateManager.colorMask(true, true, true, false);
        GlStateManager.blendFunc(GlStateManager.SourceFactor.DST_ALPHA, GlStateManager.DestFactor.ONE);
        GlStateManager.depthMask(false);
        GlStateManager.depthFunc(514);
    }

    public static void endMask() {
        cleanup();
        GlStateManager.enableTexture2D();
        GlStateManager.colorMask(true, true, true, true);
    }

    public static void cleanDrawTexture(ResourceLocation texture, int left, int top, int width, int height, float u, float v, float umax, float vmax) {
        cleanup();
        drawTexture(texture, left, top, width, height, u, v, umax, vmax);
    }

    public static void drawTexture(ResourceLocation texture, int left, int top, int width, int height, float u, float v, float umax, float vwidth) {
        Minecraft.getMinecraft().getTextureManager().bindTexture(texture);
        Gui.drawModalRectWithCustomSizedTexture(left, top, u, v, width, height, umax, vwidth);
    }

    public static void drawRect(int width, int height, float r, float g, float b, float a) {
        GlStateManager.disableTexture2D();
        GlStateManager.alphaFunc(GL11.GL_GREATER, 0F);
        GlStateManager.color(r, g, b, a);
        Tessellator tessellator = Tessellator.getInstance();
        VertexBuffer buffer = tessellator.getBuffer();
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION);
        buffer.pos(0, height, 0).endVertex();
        buffer.pos(width, height, 0).endVertex();
        buffer.pos(width, 0, 0).endVertex();
        buffer.pos(0, 0, 0).endVertex();
        tessellator.draw();
        GlStateManager.enableTexture2D();
    }

    public static void drawItemStack(ItemStack stack, int x, int y) {
        if (stack == null) {
            return;
        }

        GlStateManager.enableBlend();
        Minecraft.getMinecraft().getRenderItem().renderItemIntoGUI(stack, x, y);
        GlStateManager.disableBlend();
    }

    public static void drawOverlays(ItemStack stack, int x, int y) {
        Minecraft.getMinecraft().getRenderItem().renderItemOverlays(Minecraft.getMinecraft().fontRendererObj, stack, x, y);
    }

    public static void drawHighlightedItemStack(ItemStack stack, int x, int y, float scale, int color) {
        if (stack == null) {
            return;
        }

        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.translate(x, y, 100F);
        GlStateManager.scale(scale, scale, scale);
        GlStateManager.enableOutlineMode(color);
        Minecraft.getMinecraft().getRenderItem().renderItemIntoGUI(stack, -8, -8);
        GlStateManager.disableOutlineMode();
        GlStateManager.popMatrix();

        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, 300F);
        Minecraft.getMinecraft().getRenderItem().renderItemIntoGUI(stack, -8, -8);
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }

    public static void beginItems() {
        GlStateManager.enableDepth();
        RenderHelper.enableGUIStandardItemLighting();
    }

    public static void endItems() {
        GlStateManager.disableDepth();
        RenderHelper.disableStandardItemLighting();
        GlStateManager.clearColor(1, 1, 1, 1);
    }
}
