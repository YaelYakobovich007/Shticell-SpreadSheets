package sheet.impl;

import sheet.api.CellSize;

public class CellSizeImpl implements CellSize
{
    private final int rowHeightUnit;
    private final int columnWidthUnit;

    public CellSizeImpl(int rowHeightUnit, int columnWidthUnit) {
        this.rowHeightUnit = rowHeightUnit;
        this.columnWidthUnit = columnWidthUnit;
    }
    @Override
    public int getRowHeightUnit() {
        return rowHeightUnit;
    }

    @Override
    public int getColumnWidthUnit() {
        return columnWidthUnit;
    }
}
