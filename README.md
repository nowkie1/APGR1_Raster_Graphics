# Raster Graphics Application

Java application developed for the APGR1+3 computer graphics course.

## Implemented Features

- Bresenham line rasterization
- Interactive line drawing with Shift-snapping
- Polygon creation and rendering
- Flood Fill and Boundary Fill using a queue
- Scan-Line polygon filling
- Repeating checkerboard fill patterns
- Sutherland–Hodgman polygon clipping
- Interactive line endpoint and polygon vertex editing
- Polygon and vertex deletion
- Customizable fill and border colors

## Controls

| Key | Action |
|---|---|
| L | Line drawing mode |
| P | Polygon drawing mode |
| Enter | Complete polygon or clipping operation |
| F | Seed Fill mode |
| B | Toggle Flood Fill / Boundary Fill |
| S | Toggle Scan-Line filling |
| T | Toggle pattern filling |
| K | Polygon clipping mode |
| E | Edit lines and polygons |
| D | Delete selected polygon or vertex |
| C | Clear canvas |
| Shift + Drag | Snap line horizontally, vertically, or diagonally |
|Fill Color Button| Open the fill color picker|
|Border Color Button| Open the polygon border color picker|

## Implementation Notes

The application uses Java Swing for its graphical user interface. Drawing algorithms are implemented in dedicated classes, while the controller handles user input and coordinates rendering.

The rasterization methods include canvas-boundary checks. Existing seed-fill operations are invalidated when polygon geometry changes to prevent incorrect filling.

## Evaluation

The completed APGR Task 1 evaluation table is submitted separately, as required by the course instructions.