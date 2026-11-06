//Listens for mouse clicks, commands the line drawer where to paint, and tells the screen to refresh.
package control;

import model.objects.Line;
import model.objects.Point;
import model.objects.Polygon;
import model.objects.PolygonClipper;
import model.raster.*;
import view.Panel;
import view.Window;

import javax.swing.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;


public class Controller {

    private enum Mode {
        LINE, POLYGON, FILL, CLIPPING
    }


    private final Panel panel;
    private final Liner liner;
    private final PolygonRasterizer polygonRasterizer;
    private final SeedFill seedFill;
    private final ScanLine scanLine;

    private final PolygonClipper polygonClipper = new PolygonClipper();

    private Polygon currentClipPolygon = new Polygon();
    private Polygon clippedResult = new Polygon();
    private Polygon clippingBoundary = new Polygon();

    private Mode mode = Mode.LINE;
    private Point startPoint;
    private final Window window;

    private final List<Line> lines = new ArrayList<>();
    private final List<Polygon> polygons = new ArrayList<>();
    private Polygon currentPolygon = new Polygon();
    private final List<FillOperation> fills = new ArrayList<>();
    private boolean boundaryMode = false;
    private boolean scanLineEnabled = false;
    private boolean patternEnabled = false;

    private final PatternFill checkerPattern =
            new CheckerPattern(0x00BFFF, 0x004D80, 10);

    private static final int SEED_FILL_COLOR = 0x00FF00;
    private static final int SCAN_LINE_COLOR = 0x00BFFF;
    private static final int CLIPPED_RESULT_COLOR = 0xFF9500;
    private static final int CLIPPING_BOUNDARY_COLOR = 0xFFFF00;

    private int fillColor = 0x00FF00;

    public void setFillColor(int color) {
        this.fillColor = color;
    }

    private int borderColor = 0xFFFFFF;

    public void setBorderColor(int color) {
        this.borderColor = color;

        redraw();
        panel.repaint();
    }

    private record FillOperation(
            Point seed,
            int color,
            boolean boundaryMode
    )
    {}

    public Controller(Panel panel, Window window) {
        this.panel = panel;
        this.window = window;
        this.liner = new GraphicsLiner(panel.getRaster());
        this.polygonRasterizer = new PolygonRasterizer(liner);
        this.seedFill = new SeedFill(panel.getRaster());
        this.scanLine = new ScanLine(panel.getRaster());


        initObjects();
        initListeners();
    }

    private void initObjects() {
        this.startPoint = null;
        redraw();
        panel.repaint();
    }

    private void initListeners() {
        panel.setFocusable(true);
        panel.requestFocusInWindow();
        MouseAdapter mouseHandler = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    hardClear();
                    return;
                }
                if (SwingUtilities.isLeftMouseButton(e)) {
                    Point clickedPoint = new Point(e.getX(), e.getY());

                    if (mode == Mode.LINE) {
                        startPoint = clickedPoint;
                    } else if (mode == Mode.POLYGON) {
                        currentPolygon.add(clickedPoint);
                        redraw();
                        panel.repaint();
                    } else if (mode == Mode.FILL) {
                        fills.add(new FillOperation(
                                clickedPoint,
                                fillColor,
                                boundaryMode
                        ));

                        redraw();
                        panel.repaint();
                    } else if (mode == Mode.CLIPPING) {
                        currentClipPolygon.add(clickedPoint);

                        redraw();
                        panel.repaint();
                    }
                }
            }


            private Point snapToDirection(Point start, Point end) {
                int dx = end.x() - start.x();
                int dy = end.y() - start.y();

                // 1. Horizontal candidate
                Point horizontalPoint = new Point(end.x(), start.y());

                // 2. Vertical candidate
                Point verticalPoint = new Point(start.x(), end.y());

                // 3. Diagonal candidate
                int diagonalLength = (Math.abs(dx) + Math.abs(dy)) / 2;

                int diagonalX = start.x()
                        + (dx < 0 ? -diagonalLength : diagonalLength);

                int diagonalY = start.y()
                        + (dy < 0 ? -diagonalLength : diagonalLength);

                Point diagonalPoint = new Point(diagonalX, diagonalY);

                // 4. Calculate distances to mouse position
                double horizontalDistance = end.distanceTo(horizontalPoint);
                double verticalDistance = end.distanceTo(verticalPoint);
                double diagonalDistance = end.distanceTo(diagonalPoint);

                // 5. Return the closest candidate
                if (horizontalDistance <= verticalDistance
                        && horizontalDistance <= diagonalDistance) {
                    return horizontalPoint;
                }

                if (verticalDistance <= diagonalDistance) {
                    return verticalPoint;
                }

                return diagonalPoint;
            }


            @Override
            public void mouseDragged(MouseEvent e) {
                if (mode == Mode.LINE && startPoint != null && SwingUtilities.isLeftMouseButton(e)) {
                    redraw();
                    Point endPoint = new Point(e.getX(), e.getY());

                    if (e.isShiftDown()) {
                        endPoint = snapToDirection(startPoint, endPoint);
                    }

                    liner.drawLine(startPoint, endPoint);
                    panel.repaint();
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (mode == Mode.LINE && startPoint != null && SwingUtilities.isLeftMouseButton(e)) {
                    Point endPoint = new Point(e.getX(), e.getY());
                    if (e.isShiftDown()) {
                        endPoint = snapToDirection(startPoint, endPoint);
                    }
                    lines.add(new Line(startPoint, endPoint));
                    startPoint = null;

                    redraw();
                    panel.repaint();
                }
            }
        };

        // Attach for both click/press events and drag events
        panel.addMouseListener(mouseHandler);
        panel.addMouseMotionListener(mouseHandler);

        panel.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_L:
                        mode = Mode.LINE;
                        updateStatus();
                        break;

                    case KeyEvent.VK_P:
                        mode = Mode.POLYGON;
                        updateStatus();
                        break;


                    case KeyEvent.VK_ENTER:

                        if (mode == Mode.POLYGON) {
                            // Save the regular polygon
                            if (currentPolygon.size() >= 3) {
                                polygons.add(currentPolygon);
                            }

                            currentPolygon = new Polygon();

                        } else if (mode == Mode.CLIPPING) {
                            // Clip the most recently saved polygon
                            if (currentClipPolygon.size() >= 3
                                    && !polygons.isEmpty()) {

                                if (isConvex(currentClipPolygon)) {
                                    Polygon subject = polygons.getLast();

                                    clippedResult = polygonClipper.clip(
                                            subject,
                                            currentClipPolygon
                                    );

                                    clippingBoundary = currentClipPolygon;

                                } else {
                                    JOptionPane.showMessageDialog(
                                            window,
                                            "The clipping polygon must be convex!"
                                    );
                                }
                            }

                            currentClipPolygon = new Polygon();
                        }

                        redraw();
                        panel.repaint();
                        break;


                    case KeyEvent.VK_F:
                        mode = Mode.FILL;
                        updateStatus();
                        break;

                    case KeyEvent.VK_B:
                        boundaryMode = !boundaryMode;
                        updateStatus();
                        break;

                    case KeyEvent.VK_S:
                        scanLineEnabled = !scanLineEnabled;
                        updateStatus();
                        redraw();
                        panel.repaint();
                        break;

                    case KeyEvent.VK_K:
                        mode = Mode.CLIPPING;
                        currentClipPolygon = new Polygon();
                        updateStatus();
                        redraw();
                        panel.repaint();
                        break;

                    case KeyEvent.VK_T:
                        patternEnabled = !patternEnabled;
                        updateStatus();
                        redraw();
                        panel.repaint();
                        break;

                    case KeyEvent.VK_C:
                        hardClear();
                        break;
                }
            }
        });
    }

    private void updateStatus() {
        String algorithm =
                boundaryMode ? "Boundary Fill" : "Flood Fill";

        window.updateStatus(
                mode.name(),
                algorithm,
                scanLineEnabled,
                patternEnabled
        );
    }


    private boolean isConvex(Polygon polygon) {
        if (polygon.size() < 3) {
            return false;
        }

        int direction = 0;

        for (int i = 0; i < polygon.size(); i++) {
            Point a = polygon.get(i);
            Point b = polygon.get((i + 1) % polygon.size());
            Point c = polygon.get((i + 2) % polygon.size());

            long cross = (long) (b.x() - a.x()) * (c.y() - b.y())
                    - (long) (b.y() - a.y()) * (c.x() - b.x());

            if (cross == 0) {
                continue;
            }

            int sign = cross > 0 ? 1 : -1;

            if (direction == 0) {
                direction = sign;
            } else if (direction != sign) {
                return false;
            }
        }

        return direction != 0;
    }


    private void redraw() {
        panel.getRaster().clear();

        // 1. Draw saved lines
        for (Line line : lines) {
            liner.drawLine(line);
        }

        // 2. Draw saved polygons
        for (Polygon poly : polygons) {
            if (scanLineEnabled) {
                if (patternEnabled) {
                    scanLine.fillPolygon(poly, checkerPattern);
                } else {
                    scanLine.fillPolygon(poly, SCAN_LINE_COLOR);
                }
            }

            polygonRasterizer.rasterize(poly, borderColor);
        }

        // 3. Draw polygon currently in progress
        if (currentPolygon.size() > 0) {
            polygonRasterizer.rasterize(currentPolygon, borderColor);
        }

        // Replay saved fill operations

        for (FillOperation fill : fills) {

            int x = fill.seed().x();
            int y = fill.seed().y();

            if (patternEnabled) {
                // Use checkerboard pattern
                if (fill.boundaryMode()) {
                    seedFill.boundaryFill(
                            x, y, checkerPattern, borderColor
                    );
                } else {
                    seedFill.floodFill(
                            x, y, checkerPattern
                    );
                }

            } else {
                // Use solid green
                if (fill.boundaryMode()) {
                    seedFill.boundaryFill(
                            x, y, fill.color(), borderColor
                    );
                } else {
                    seedFill.floodFill(
                            x, y, fill.color()
                    );
                }
            }
        }


        // Draw the clipped polygon
        if (clippedResult.size() >= 3) {
            scanLine.fillPolygon(clippedResult, CLIPPED_RESULT_COLOR);
            polygonRasterizer.rasterize(clippedResult);
        }

        // Show the completed clipping boundary in yellow
        if (clippingBoundary.size() >= 3) {
            polygonRasterizer.rasterize(clippingBoundary, CLIPPING_BOUNDARY_COLOR);
        }

        // Show the clipping polygon currently being drawn in yellow
        if (mode == Mode.CLIPPING && currentClipPolygon.size() >= 2) {
            polygonRasterizer.rasterize(currentClipPolygon, CLIPPING_BOUNDARY_COLOR);
        }
    }

    private void hardClear() {
        lines.clear();
        polygons.clear();
        fills.clear();
        currentPolygon = new Polygon();
        startPoint = null;

        currentClipPolygon = new Polygon();
        clippingBoundary = new Polygon();
        clippedResult = new Polygon();

        panel.getRaster().clear();
        panel.repaint();
    }
}