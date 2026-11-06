package model.raster;

import java.util.Collections;
import java.util.List;
import java.util.ArrayList;
import model.objects.Point;
import model.objects.Polygon;

public class ScanLine {

    private final Raster raster;

    public ScanLine(Raster raster){
        this.raster = raster;
    }

    public void fillPolygon(Polygon polygon, PatternFill pattern){

        if(polygon.size() < 3) {
            return;
        }

        int minY = polygon.get(0).y();
        int maxY = polygon.get(0).y();

        // Find the minimum and maximum Y coordinates
        for(int i = 1; i < polygon.size(); i++){
            Point point = polygon.get(i);

            minY = Math.min(minY, point.y());
            maxY = Math.max(maxY, point.y());
        }

        // Go through each horizontal scan line
        for(int y = minY; y < maxY; y++){
            List<Double> intersections = new ArrayList<>();
            for(int i = 0; i < polygon.size(); i++) {
                Point p1 = polygon.get(i);
                //Connecting last vertex back to the first one
                Point p2 = polygon.get((i + 1) % polygon.size());

                int yMin = Math.min(p1.y(), p2.y());
                int yMax = Math.max(p1.y(), p2.y());

                // Check if scan line crosses this edge
                if(y >= yMin && y < yMax) {
                    double x = p1.x() + (double) (y - p1.y()) * (p2.x() - p1.x()) / (p2.y() - p1.y());

                    intersections.add(x);
                }
            }

            Collections.sort(intersections);

            // Fill between pairs of intersections
            for (int i = 0; i + 1 < intersections.size(); i += 2) {

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

    public void fillPolygon(Polygon polygon, int fillColor) {
        fillPolygon(polygon, (x, y) -> fillColor);
    }

}
