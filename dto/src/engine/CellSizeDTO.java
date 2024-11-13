package engine;

import sheet.api.CellSize;

public class CellSizeDTO {

    private final int rowHeightUnit;
    private final int columnWidthUnit;

    public CellSizeDTO(CellSize cellSize) {
        this.rowHeightUnit = cellSize.getRowHeightUnit();
        this.columnWidthUnit = cellSize.getColumnWidthUnit();
    }

    public int getRowHeightUnit() {
        return rowHeightUnit;
    }

    public int getColumnWidthUnit() {
        return columnWidthUnit;
    }
}
