//Implements the pixel grid by actually storing and coloring pixels inside a Java BufferedImage.
package model.raster;

import java.awt.image.BufferedImage;

public class RasterBI implements Raster {

    private final BufferedImage image;
    private int clearColor;

    public RasterBI(int w, int h) {
        image = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        clearColor = 0x161633;
    }

    @Override
    public void clear() {
        for (int x = 0; x < image.getWidth(); x++) { // w = 800
            for (int y = 0; y < image.getHeight(); y++) {
                image.setRGB(x, y, clearColor);
            }
        }

    }

    @Override
    public void setPixel(int x, int y, int color) {
        image.setRGB(x, y, color);
    }

    @Override
    public int getPixel(int x, int y) {
        return image.getRGB(x, y) & 0xFFFFFF;
    }

    @Override
    public int getWidth() {
        return image.getWidth();
    }

    @Override
    public int getHeight() {
        return image.getHeight();
    }

    public BufferedImage getImage() {
        return image;
    }
}
