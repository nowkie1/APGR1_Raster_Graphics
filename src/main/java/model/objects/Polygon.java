package model.objects;

import java.util.ArrayList;
import java.util.List;

public class Polygon {
    private List<Point> polygon;

    public Polygon() {
        polygon = new ArrayList<>();
    }

    public void add(Point p) {
        polygon.add(p);
    }

    public void AddMore(Point... p) {
        polygon.addAll(List.of(p));
    }

    public int size() {
        return polygon.size();
    }

    public Point get(int index) {
        return polygon.get(index);
    }

}
