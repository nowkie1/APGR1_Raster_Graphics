package model.raster;

import model.objects.Point;
import model.objects.Polygon;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
/**
 * Implements the Scan-Line polygon filling algorithm.
 * For each horizontal scan line, the algorithm calculates
 * its intersections with the polygon edges, sorts them,
 * and fills pixels between pairs of intersections.
 * Supports solid colors and repeating fill patterns.
 */
public class ScanLine {

    private final Raster raster;

    public ScanLine(Raster raster) {
        this.raster = raster;
    }

    public void fillPolygon(Polygon polygon, PatternFill pattern) {

        if (polygon.size() < 3) {
            return;
        }

        int minY = polygon.get(0).y();
        int maxY = polygon.get(0).y();

        // Find the minimum and maximum Y coordinates
        for (int i = 1; i < polygon.size(); i++) {
            Point point = polygon.get(i);

            minY = Math.min(minY, point.y());
            maxY = Math.max(maxY, point.y());
        }

        // Go through each horizontal scan line
        for (int y = Math.max(0, minY);
             y < Math.min(maxY, raster.getHeight());
             y++) {
            List<Double> intersections = new ArrayList<>();
            for (int i = 0; i < polygon.size(); i++) {
                Point p1 = polygon.get(i);
                //Connecting last vertex back to the first one
                Point p2 = polygon.get((i + 1) % polygon.size());

                int yMin = Math.min(p1.y(), p2.y());
                int yMax = Math.max(p1.y(), p2.y());

                // Include the lower endpoint but exclude the upper one
                // to avoid counting shared vertices twice.
                if (y >= yMin && y < yMax) {
                    // Calculate the X coordinate where this edge crosses the scan line.
                    double x = p1.x() + (double) (y - p1.y())
                            * (p2.x() - p1.x()) / (p2.y() - p1.y());

                    intersections.add(x);
                }
            }
            // Sort intersections from left to right.
            Collections.sort(intersections);

            // Apply the even-odd rule: fill between each pair of intersections.
            for (int i = 0; i + 1 < intersections.size(); i += 2) {
                // Convert intersection positions to integer pixel coordinates
                // and restrict filling to the visible canvas width.
                int xStart = Math.max(0,
                        (int) Math.ceil(intersections.get(i)));

                int xEnd = Math.min(raster.getWidth() - 1,
                        (int) Math.floor(intersections.get(i + 1)));

                for (int x = xStart; x <= xEnd; x++) {
                    raster.setPixel(x, y, pattern.getColor(x, y));
                }

            }
        }
    }
    // Reuse pattern-based filling with a constant color.
    public void fillPolygon(Polygon polygon, int fillColor) {
        fillPolygon(polygon, (x, y) -> fillColor);
    }

}
