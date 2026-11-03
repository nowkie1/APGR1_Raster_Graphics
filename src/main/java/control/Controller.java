//Listens for mouse clicks, commands the line drawer where to paint, and tells the screen to refresh.
package control;

import model.objects.Line;
import model.objects.Point;
import model.objects.Polygon;
import model.raster.BresenhamLiner;
import model.raster.GraphicsLiner;
import model.raster.Liner;
import model.raster.PolygonRasterizer;
import model.raster.SeedFill;
import model.raster.ScanLine;
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
        LINE, POLYGON, FILL
    }


    private final Panel panel;
    private Liner liner;
    private final PolygonRasterizer polygonRasterizer;
    private final SeedFill seedFill;
    private final ScanLine scanLine;

    private Mode mode = Mode.LINE;
    private Point startPoint;
    private final Window window;

    private final List<Line> lines = new ArrayList<>();
    private final List<Polygon> polygons = new ArrayList<>();
    private Polygon currentPolygon = new Polygon();
    private final List<FillOperation> fills = new ArrayList<>();
    private boolean boundaryMode = false;
    private boolean scanLineEnabled = false;

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
        //this.liner = new BresenhamLiner(panel.getRaster());
        this.startPoint = null;
        // Test lines from assignment README:
//        liner.drawLine(new Point(50, 200), new Point(200, 50));   // l1
//        liner.drawLine(new Point(300, 50), new Point(450, 200));  // l2
//        liner.drawLine(new Point(200, 350), new Point(50, 500));  // l3
//        liner.drawLine(new Point(450, 500), new Point(300, 350)); // l4
//        liner.drawLine(new Point(650, 50), new Point(650, 200));  // l5
//        liner.drawLine(new Point(650, 350), new Point(650, 500)); // l6
//        liner.drawLine(new Point(50, 550), new Point(300, 550));  // l7
//        liner.drawLine(new Point(400, 550), new Point(650, 550)); // l8
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
                                0x00FF00,
                                boundaryMode
                        ));

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
                        // Finalize the in-progress polygon
                        if (currentPolygon.size() >= 3) {
                            polygons.add(currentPolygon);
                        }
                        currentPolygon = new Polygon();
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
                scanLineEnabled
        );
    }

    private void redraw() {
        panel.getRaster().clear();

        // 1. Draw saved lines
        for (Line line : lines) {
            liner.drawLine(line);
        }

        // 2. Draw saved polygons
        for (Polygon poly : polygons) {
            if(scanLineEnabled) {
                scanLine.fillPolygon(poly, 0x00FF00);
            }
            polygonRasterizer.rasterize(poly);
        }

        // 3. Draw polygon currently in progress
        if (currentPolygon.size() > 0) {
            polygonRasterizer.rasterize(currentPolygon);
        }

        // Replay saved fill operations
        for (FillOperation fill : fills) {
            if (fill.boundaryMode()) {
                seedFill.boundaryFill(
                        fill.seed().x(),
                        fill.seed().y(),
                        fill.color(),
                        0xFFFFFF
                );
            } else {
                seedFill.floodFill(
                        fill.seed().x(),
                        fill.seed().y(),
                        fill.color()
                );
            }
        }
    }

    private void hardClear() {
        lines.clear();
        polygons.clear();
        fills.clear();
        currentPolygon = new Polygon();
        startPoint = null;

        panel.getRaster().clear();
        panel.repaint();
    }
}