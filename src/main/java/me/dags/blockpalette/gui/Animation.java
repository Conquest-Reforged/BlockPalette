package me.dags.blockpalette.gui;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * @author dags <dags@dags.me>
 */
@SideOnly(Side.CLIENT)
public class Animation {

    private final Positional positional;
    private final int xIncrement;
    private final int yIncrement;
    private final int startX;
    private final int startY;
    private final int endX;
    private final int endY;

    private int xPos;
    private int yPos;
    private long time = 0L;
    private boolean complete = false;

    public Animation(Positional positional, int time, int startX, int startY, int endX, int endY) {
        this.xPos = startX;
        this.yPos = startY;
        this.endX = endX;
        this.endY = endY;
        this.startX = startX;
        this.startY = startY;
        this.positional = positional;
        this.xIncrement = (endX - startX) / time;
        this.yIncrement = (endY - startY) / time;
    }

    public void reset() {
        this.xPos = startX;
        this.yPos = startY;
        this.complete = false;
    }

    public boolean isComplete() {
        return complete;
    }

    public void tick() {
        if (!complete) {
            long current = System.currentTimeMillis();
            if (current - time > 10L) {
                this.time = current;

                xPos = clamp(xPos + xIncrement, startX, endX);
                yPos = clamp(yPos + yIncrement, startY, endY);

                positional.setPosition(xPos, yPos);
                complete = xPos == endX && yPos == endY;
            }
        }
    }

    private static int clamp(int pos, int start, int end) {
        if (end > start) {
            return Math.min(pos, end);
        }
        return Math.max(pos, end);
    }
}
