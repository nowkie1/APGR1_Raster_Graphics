package model.raster;

public class CheckerPattern implements PatternFill {

    private final int color1;
    private final int color2;
    private final int cellSize;

    public CheckerPattern(int color1, int color2, int cellSize) {
        if (cellSize <= 0) {
            throw new IllegalArgumentException(
                    "Cell size must be positive"
            );
        }

        this.color1 = color1;
        this.color2 = color2;
        this.cellSize = cellSize;
    }

    @Override
    public int getColor(int x, int y) {
        int column = Math.floorDiv(x, cellSize);
        int row = Math.floorDiv(y, cellSize);

        if ((column + row) % 2 == 0) {
            return color1;
        }

        return color2;
    }
}
