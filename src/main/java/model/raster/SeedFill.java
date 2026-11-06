
package model.raster;

import model.objects.Point;
import java.util.ArrayDeque;
import java.util.Queue;

public class SeedFill {

    private final Raster raster;

    public SeedFill(Raster raster) {
        this.raster = raster;
    }

    // Solid flood fill
    public void floodFill(int x, int y, int fillColor) {
        floodFill(x, y, (px, py) -> fillColor);
    }

    // Patterned flood fill
    public void floodFill(int x, int y, PatternFill pattern) {
        fill(x, y, pattern, false, 0);
    }

    // Solid boundary fill
    public void boundaryFill(int x, int y,
                             int fillColor, int boundaryColor) {
        boundaryFill(x, y, (px, py) -> fillColor, boundaryColor);
    }

    // Patterned boundary fill
    public void boundaryFill(int x, int y,
                             PatternFill pattern, int boundaryColor) {
        fill(x, y, pattern, true, boundaryColor);
    }

    private void fill(int x, int y, PatternFill pattern,
                      boolean boundaryMode, int boundaryColor) {

        int width = raster.getWidth();
        int height = raster.getHeight();

        if (x < 0 || x >= width || y < 0 || y >= height) {
            return;
        }

        int targetColor = raster.getPixel(x, y);

        if (boundaryMode && targetColor == boundaryColor) {
            return;
        }

        boolean[][] visited = new boolean[height][width];

        Queue<Point> queue = new ArrayDeque<>();
        queue.add(new Point(x, y));

        while (!queue.isEmpty()) {
            Point current = queue.poll();

            int px = current.x();
            int py = current.y();

            if (px < 0 || px >= width ||
                    py < 0 || py >= height) {
                continue;
            }

            if (visited[py][px]) {
                continue;
            }

            visited[py][px] = true;

            int currentColor = raster.getPixel(px, py);

            if (boundaryMode) {
                if (currentColor == boundaryColor) {
                    continue;
                }
            } else {
                if (currentColor != targetColor) {
                    continue;
                }
            }

            raster.setPixel(px, py, pattern.getColor(px, py));

            queue.add(new Point(px + 1, py));
            queue.add(new Point(px - 1, py));
            queue.add(new Point(px, py + 1));
            queue.add(new Point(px, py - 1));
        }
    }
}
