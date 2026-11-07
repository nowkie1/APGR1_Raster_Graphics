//A blueprint defining basic pixel grid actions
//(like coloring a pixel, reading a pixel, or clearing the screen) without caring how they are stored.
package model.raster;

//An interface in Java is a contract (or blueprint) that defines what a class must do, but not how it does it.
public interface Raster {

    void clear();

    void setPixel(int x, int y, int color);

    /**
     * Returns a color of specific pixel.
     *
     * @param x column coordinate
     * @param y row coordinate
     * @return color in integer
     */
    int getPixel(int x, int y);

    int getWidth();

    int getHeight();


}
