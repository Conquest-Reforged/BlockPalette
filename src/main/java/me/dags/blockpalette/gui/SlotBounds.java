package me.dags.blockpalette.gui;

import me.dags.blockpalette.util.Config;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author dags <dags@dags.me>
 */
@SideOnly(Side.CLIENT)
public class SlotBounds {

    private final List<List<Point>> bounds = new ArrayList<>();
    private List<Point> points = new ArrayList<>();

    public SlotBounds startNew() {
        bounds.add(points = new ArrayList<>());
        return this;
    }

    public void add(Point point) {
        points.add(point);
    }

    public void draw(float red, float green, float blue, float ticks) {
        GlStateManager.color(red, green, blue, Config.color_opacity);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        for (List<Point> points : bounds) {
            buffer.begin(GL11.GL_POLYGON, DefaultVertexFormats.POSITION);
            for (Point point : points) {
                buffer.pos(point.x, point.y, 0).endVertex();
            }
            tessellator.draw();
        }
    }
}
