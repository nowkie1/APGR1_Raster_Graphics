package model.objects;

import java.util.ArrayList;
import java.util.List;

public class Polygon {
    private final List<Point> polygon;

    public Polygon() {
        polygon = new ArrayList<>();
    }

    public void set(int index, Point point) {
        polygon.set(index, point);
    }

    public void add(Point p) {
        polygon.add(p);
    }

    public int size() {
        return polygon.size();
    }

    public Point get(int index) {
        return polygon.get(index);
    }

    public void remove(int index) {
        polygon.remove(index);
    }

}
