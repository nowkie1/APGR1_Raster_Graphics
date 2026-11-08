package model.raster;

import model.objects.Point;
import model.objects.Polygon;

public class PolygonRasterizer {
    private final Liner liner;

    public PolygonRasterizer(Liner liner) {
        this.liner = liner;
    }

    // Draw polygon using the default white color
    public void rasterize(Polygon ngon) {
        rasterize(ngon, 0xFFFFFF);
    }


    public void rasterize(Polygon ngon, int color) {
        if (ngon.size() < 2) {
            return;
        }

        if (ngon.size() == 2) {
            liner.drawLine(ngon.get(0), ngon.get(1), color);
            return;
        }

        if (ngon.size() > 2) {
            for (int i = 0; i < ngon.size(); i++) {
                // Connect the last vertex to the first
                Point a = ngon.get(i);
                Point b = ngon.get((i + 1) % ngon.size()); // % wraps up to first index point at the end

                liner.drawLine(a, b, color);
            }
        }
    }
}
