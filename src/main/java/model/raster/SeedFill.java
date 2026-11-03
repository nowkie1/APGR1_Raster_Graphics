package model.raster;
import java.util.ArrayDeque;
import java.util.Queue; //The Queue is a Java collection that uses the FIFO principle (First In, First Out).
import model.objects.Point;

public class SeedFill {

    private final Raster raster;

    public SeedFill(Raster raster) {
        this.raster = raster;
    }

    public void floodFill(int x, int y, int fillColor) {
        // Check that the starting point is inside the raster
        if (x < 0 || x >= raster.getWidth()
                || y < 0 || y >= raster.getHeight()) {
            return;
        }

        int targetColor = raster.getPixel(x, y);

        if (targetColor == fillColor) {
            return;
        }

        Queue<Point> queue = new ArrayDeque<>();
        queue.add(new Point(x, y));

        while (!queue.isEmpty()) {
            Point current = queue.poll();

            int px = current.x();
            int py = current.y();

            if (px < 0 || px >= raster.getWidth()
                    || py < 0 || py >= raster.getHeight()) {
                continue;
            }

            if (raster.getPixel(px, py) != targetColor) {
                continue;
            }

            raster.setPixel(px, py, fillColor);

            queue.add(new Point(px + 1, py));
            queue.add(new Point(px - 1, py));
            queue.add(new Point(px, py + 1));
            queue.add(new Point(px, py - 1));
        }
    }


    public void boundaryFill(int x, int y,
                             int fillColor, int boundaryColor) {

        if (x < 0 || x >= raster.getWidth()
                || y < 0 || y >= raster.getHeight()) {
            return;
        }

        Queue<Point> queue = new ArrayDeque<>();
        queue.add(new Point(x, y));

        while (!queue.isEmpty()) {
            Point current = queue.poll();

            int px = current.x();
            int py = current.y();

            // Stay inside raster
            if (px < 0 || px >= raster.getWidth()
                    || py < 0 || py >= raster.getHeight()) {
                continue;
            }

            int currentColor = raster.getPixel(px, py);

            // Stop at the boundary or already filled pixels
            if (currentColor == boundaryColor
                    || currentColor == fillColor) {
                continue;
            }

            // Fill current pixel
            raster.setPixel(px, py, fillColor);

            // Visit four neighbors
            queue.add(new Point(px + 1, py));
            queue.add(new Point(px - 1, py));
            queue.add(new Point(px, py + 1));
            queue.add(new Point(px, py - 1));
        }
    }

}

