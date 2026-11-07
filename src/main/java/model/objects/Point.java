//Stores a single, unchangeable 2D coordinate on a screen
package model.objects;

// A Java record is a concise class designed purely to carry immutable (unchangeable) data.
public record Point(int x, int y) {

    public double distanceTo(Point other) {
        int dx = this.x - other.x();
        int dy = this.y - other.y();
        return Math.sqrt(dx * dx + dy * dy);
    }

}