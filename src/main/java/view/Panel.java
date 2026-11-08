//Displays the drawn pixel image directly inside the window frame.
package view;

import model.raster.Raster;
import model.raster.RasterBI;

import javax.swing.*;
import java.awt.*;

public class Panel extends JPanel {

    private final RasterBI raster;

    // package-protected constructor
    Panel(int w, int h) {
        setFocusable(true);
        setPreferredSize(new Dimension(w, h));
        raster = new RasterBI(w, h);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(raster.getImage(), 0, 0, null);
    }

    public Raster getRaster() {
        return raster;
    }
}
