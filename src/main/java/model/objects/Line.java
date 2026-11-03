//An immutable data record representing a line segment defined by its two endpoint coordinates.
package model.objects;

public record Line(Point p1, Point p2) {
    public Line(int x1, int y1, int x2, int y2) {
        this(new Point(x1, y1), new Point(x2, y2));
    }
}
