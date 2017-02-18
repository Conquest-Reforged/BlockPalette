package me.dags.blockpalette.gui;

import java.awt.*;

/**
 * @author dags <dags@dags.me>
 */
public class Edge {

    private final double angle;
    private final double partialAngle;
    private final double length;
    private final double outerLength;
    private final double innerLength;

    final Point start;
    final Point end;
    final Point outerStart;
    final Point outerEnd;
    final Point innerStart;
    final Point innerEnd;

    public Edge(Point cStart, Point oStart, Point iStart, int centerX, int centerY, int radius, int outerPadding, int innerPadding, double angle, double angleIncrement) {
        double rads = Math.toRadians(angle);
        this.start = cStart;
        this.end = toPoint(centerX, centerY, radius, rads);
        this.outerStart = oStart;
        this.outerEnd = toPoint(centerX, centerY, radius + outerPadding, rads);
        this.innerStart = iStart;
        this.innerEnd = toPoint(centerX, centerY, radius - innerPadding, rads);
        this.angle = angle;
        this.partialAngle = angleIncrement;
        this.length = start.distance(end);
        this.outerLength = outerStart.distance(outerEnd);
        this.innerLength = innerStart.distance(innerEnd);
    }

    public double getAngle() {
        return angle;
    }

    public Point getPosition(double angle) {
        return getPosition(start, end, length, angle, partialAngle);
    }

    public Point getOuterPosition(double angle) {
        return getPosition(outerStart, outerEnd, outerLength, angle, partialAngle);
    }

    public Point getInnerPosition(double angle) {
        return getPosition(innerStart, innerEnd, innerLength, angle, partialAngle);
    }

    private static Point getPosition(Point start, Point end, double length, double angle, double partialAngle) {
        double progress = (angle % partialAngle) / partialAngle;
        double dl = progress * length;
        double t = dl / length;
        int x = (int) Math.round((1 - t) * start.getX() + t * end.getX());
        int y = (int) Math.round((1 - t) * start.getY() + t * end.getY());
        return new Point(x, y);
    }

    static Point toPoint(int cX, int cY, int radius, double rads) {
        int x = (int) (Math.round(cX + Math.cos(rads) * radius));
        int y = (int) (Math.round(cY + Math.sin(rads) * radius));
        return new Point(x, y);
    }
}
