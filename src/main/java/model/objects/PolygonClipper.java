package model.objects;

import java.util.ArrayList;
import java.util.List;
/**
 * Implements the Sutherland-Hodgman polygon clipping algorithm.
 * Clips a subject polygon against a convex clipping polygon
 * by processing each clipping edge separately.
 * Supports clockwise and counterclockwise clipping polygons.
 */
public class PolygonClipper {

    public Polygon clip(Polygon subject, Polygon clipper) {

        if (subject.size() < 3 || clipper.size() < 3) {
            return new Polygon();
        }

        double area = signedArea(clipper);

        if (area == 0) {
            return new Polygon();
        }

        // In screen coordinates (Y increases downward),
        // a positive signed area indicates clockwise orientation.
        boolean clockwise = area > 0;

        // Copy the subject polygon's vertices
        List<Point> output = new ArrayList<>();

        for (int i = 0; i < subject.size(); i++) {
            output.add(subject.get(i));
        }

        // Process each clipping edge
        for (int i = 0; i < clipper.size(); i++) {

            Point a = clipper.get(i);
            Point b = clipper.get((i + 1) % clipper.size());

            List<Point> input = new ArrayList<>(output);
            output.clear();

            if (input.isEmpty()) {
                break;
            }

            Point previous = input.getLast();

            for (Point current : input) {

                boolean previousInside =
                        isInside(previous, a, b, clockwise);

                boolean currentInside =
                        isInside(current, a, b, clockwise);

                // Apply the four Sutherland-Hodgman cases:
                // inside-inside, inside-outside,
                // outside-inside, and outside-outside.
                if (previousInside && currentInside) {
                    output.add(current);

                } else if (previousInside) {
                    output.add(intersection(previous, current, a, b));

                } else if (currentInside) {
                    output.add(intersection(previous, current, a, b));
                    output.add(current);
                }

                previous = current;
            }
        }

        Polygon result = new Polygon();

        for (Point point : output) {
            result.add(point);
        }

        return result;
    }

    /**
     * Calculates the cross product of three points.
     * Used to determine which side of a directed edge
     * a point is located on.
     */
    private double crossProduct(Point a, Point b, Point p) {
        return (double) (b.x() - a.x()) * (p.y() - a.y())
                - (double) (b.y() - a.y()) * (p.x() - a.x());
    }

    /**
     * Calculates the signed area of a polygon using
     * the Shoelace Formula.
     * The sign determines whether the vertices are
     * ordered clockwise or counterclockwise.
     */
    private double signedArea(Polygon polygon) {
        double sum = 0;

        for (int i = 0; i < polygon.size(); i++) {
            Point a = polygon.get(i);
            Point b = polygon.get((i + 1) % polygon.size());

            sum += (double) a.x() * b.y()
                    - (double) b.x() * a.y();
        }

        return sum / 2.0;
    }

    /**
     * Checks whether a point is inside a clipping edge.
     * Uses the cross product and the polygon's orientation
     * to determine which side is considered inside.
     */
    private boolean isInside(
            Point p,
            Point a,
            Point b,
            boolean clockwise
    ) {
        double cross = crossProduct(a, b, p);

        if (clockwise) {
            return cross >= 0;
        } else {
            return cross <= 0;
        }
    }

    /**
     * Calculates where a subject polygon edge intersects
     * the infinite line containing a clipping edge.
     * Uses linear interpolation and rounds the result
     * to integer raster coordinates.
     */
    private Point intersection(
            Point previous,
            Point current,
            Point a,
            Point b
    ) {
        double crossPrevious = crossProduct(a, b, previous);
        double crossCurrent = crossProduct(a, b, current);

        double denominator = crossPrevious - crossCurrent;

        if (Math.abs(denominator) < 1e-10) {
            return previous;
        }

        double t = crossPrevious / denominator;

        double x = previous.x()
                + t * (current.x() - previous.x());

        double y = previous.y()
                + t * (current.y() - previous.y());

        return new Point(
                (int) Math.round(x),
                (int) Math.round(y)
        );
    }

}
