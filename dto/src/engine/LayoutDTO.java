package engine;

import sheet.api.CellSize;
import sheet.api.Layout;

public class LayoutDTO {
    private final int rows;
    private final int columns;
    private final CellSizeDTO size;

    public LayoutDTO(Layout layout){
        this.rows= layout.getNumOfRows();
        this.columns= layout.getNumOfColumns();
        this.size= new CellSizeDTO(layout.getLayoutSize());
    }

    public int getNumOfRows() {
        return rows;
    }

    public int getNumOfColumns() {
        return columns;
    }

    public CellSizeDTO getLayoutSize() {
        return size;
    }
}
