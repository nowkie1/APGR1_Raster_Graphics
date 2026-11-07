package model.raster;
/**
 * Implements Bresenham's line rasterization algorithm.
 * Uses integer arithmetic to draw line segments in all eight octants.
 * X-major and Y-major lines are handled separately.
 */
public class BresenhamLiner extends Liner {

    public BresenhamLiner(Raster raster) {
        super(raster);
    }

    @Override
    protected void rasterize(int x0, int y0, int x1, int y1) {
        if (Math.abs(x1 - x0) > Math.abs(y1 - y0)) {
            drawXMajorLine(x0, y0, x1, y1);
        } else {
            drawYMajorLine(x0, y0, x1, y1);
        }
    }

    //Method for drawing a horizontal line
    private void drawXMajorLine(int x0, int y0, int x1, int y1) {
        // Swap endpoints so the loop always steps forward from the smaller X to the larger X.
        if (x0 > x1) {
            int tempX = x0;
            x0 = x1;
            x1 = tempX;
            int tempY = y0;
            y0 = y1;
            y1 = tempY;
        }

        int dx = x1 - x0;
        int dy = y1 - y0;

        // Determine the vertical direction and use the absolute change in Y.
        int dir = (dy < 0) ? -1 : 1;
        dy *= dir;

        int y = y0;
        // Decision variable determines when to move to the next row.
        int p = 2 * dy - dx;
        // Iterate horizontally across all columns from x0 to x1
        for (int i = 0; i <= dx; i++) {
            raster.setPixel(x0 + i, y, color);
            // Choose the closer pixel: if (d0 >= d1), the line is closer to the next row
            if (p >= 0) {
                y += dir; // Take a vertical step in the line's direction
                p -= 2 * dx; // Taking a diagonal step(step down the pixel)
            }
            // Stayed on the same row
            p += 2 * dy;
        }
    }

    //Method for drawing a vertical line
    private void drawYMajorLine(int x0, int y0, int x1, int y1) {
        if (y0 > y1) {
            int tempX = x0;
            x0 = x1;
            x1 = tempX;
            int tempY = y0;
            y0 = y1;
            y1 = tempY;
        }

        int dx = x1 - x0;
        int dy = y1 - y0;

        int dir = (dx < 0) ? -1 : 1;
        dx *= dir;

        int x = x0;
        int p = 2 * dx - dy;

        for (int i = 0; i <= dy; i++) {
            raster.setPixel(x, y0 + i, color);

            if (p >= 0) {
                x += dir;
                p -= 2 * dy;
            }
            p += 2 * dx;
        }
    }
}