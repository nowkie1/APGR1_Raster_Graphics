package model.raster;

import java.awt.*;

public class GraphicsLiner extends Liner{

    public GraphicsLiner(Raster raster) {
        super(raster);
    }

    @Override
    protected void rasterize(int x1, int y1, int x2, int y2) {
        Graphics g = ((RasterBI)raster).getImage().getGraphics();
        g.setColor(new Color(color));
        g.drawLine(x1, y1, x2, y2);

    }
}