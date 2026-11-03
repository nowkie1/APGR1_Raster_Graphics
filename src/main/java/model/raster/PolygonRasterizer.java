package model.raster;

import model.objects.Point;
import model.objects.Polygon;

public class PolygonRasterizer {
    private Liner liner;

    public PolygonRasterizer(Liner liner) {
        this.liner = liner;
    }

    public void rasterize(Polygon ngon) {
        if(ngon.size() < 2) {
            return;
        }

        if(ngon.size() == 2) {
            liner.drawLine(ngon.get(0), ngon.get(1));
            return;
        }

        if(ngon.size() > 2){
            for(int i = 0; i < ngon.size(); i++){
                //connect last with first
                Point a  = ngon.get(i);
                Point b = ngon.get((i+1) % ngon.size()); // % wraps up to first index point at the end

                liner.drawLine(a, b);
            }
        }
    }
}
