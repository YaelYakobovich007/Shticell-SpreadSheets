package sheet.impl;

import sheet.api.Layout;
import sheet.api.CellSize;

public class LayoutImpl implements Layout {
    private final int rows;
    private final int columns;
    private final CellSize size;

    public LayoutImpl(int rows, int columns, CellSize size) {
        this.rows = rows;
        this.columns = columns;
        this.size = size;
    }

    @Override
    public int getNumOfRows() {
        return rows;
    }

    @Override
    public int getNumOfColumns() {
        return columns;
    }

    @Override
    public CellSize getLayoutSize() {
        return size;
    }


}
