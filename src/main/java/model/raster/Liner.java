//A shared base template for any line-drawing algorithm that manages target colors and common drawing commands.
package model.raster;

import model.objects.Line;
import model.objects.Point;

public abstract class Liner {
    Raster raster;
    int color;

    public Liner(Raster raster) {
        this.raster = raster;
        color = 0xFFFFFF; // white color
    }

    protected abstract void rasterize(int x1, int y1, int x2, int y2);

    // method overload:
    public void drawLine(int x1, int y1, int x2, int y2) {
        rasterize(x1, y1, x2, y2);
    }

    public void drawLine(Point a, Point b) {
        rasterize(a.x(), a.y(), b.x(), b.y());
    }

    public void drawLine(Line line) {
        rasterize(line.p1().x(), line.p1().y(), line.p2().x(), line.p2().y());
    }

    public void drawLine(Point a, Point b, int color) {
        int previousColor = this.color;

        this.color = color;
        rasterize(a.x(), a.y(), b.x(), b.y());

        this.color = previousColor;
    }


}