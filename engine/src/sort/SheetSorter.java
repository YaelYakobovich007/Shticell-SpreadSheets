package sort;

import sheet.api.Sheet;
import sheet.cell.api.Cell;
import sheet.cell.api.EffectiveValue;
import sheet.cell.impl.CellImpl;
import sheet.coordinate.Coordinate;
import sheet.coordinate.CoordinateFactory;
import java.util.*;

public class SheetSorter {
    public Sheet sort(Sheet copySheet, Coordinate from, Coordinate to, List<Character> selectedColumns) {
        List<List<Cell>> rows = collectRows(copySheet, from, to);
        rows.sort((row1, row2) -> compareRowsByColumns(row1, row2, selectedColumns, from));
        Map<Coordinate, Cell> newActiveCells = updateCellsInSheet(copySheet, rows, from, to);
        copySheet.setActiveCell(newActiveCells);
        return copySheet;
    }

    private List<List<Cell>> collectRows(Sheet copySheet, Coordinate from, Coordinate to) {
        List<List<Cell>> rows = new ArrayList<>();
        for (int row = from.getRow(); row <= to.getRow(); row++) {
            List<Cell> rowCells = new ArrayList<>();
            for (int col = from.getColumn(); col <= to.getColumn(); col++) {
                Cell cell = copySheet.getCell(row, col);
                if (cell != null) {
                    rowCells.add(cell);
                } else {
                    Cell emptyCell = new CellImpl(row, col, String.valueOf(Double.MAX_VALUE), copySheet.getVersionNumber(),copySheet.getOwnerName());
                    rowCells.add(emptyCell);
                }
            }
            rows.add(rowCells);
        }
        return rows;
    }

    private int compareRowsByColumns(List<Cell> row1, List<Cell> row2, List<Character> selectedColumns, Coordinate from) {
        for (char col : selectedColumns) {
            int colIndex = getColumnIndex(col, from);
            EffectiveValue value1 = row1.get(colIndex).getEffectiveValue();
            EffectiveValue value2 = row2.get(colIndex).getEffectiveValue();
            int cmp = compareValues(value1, value2);
            if (cmp != 0) {
                return cmp;
            }
        }
        return 0;
    }

    private int getColumnIndex(char col, Coordinate from) {
        return (col - 'A' + 1) - from.getColumn();
    }


    private int compareValues(EffectiveValue value1, EffectiveValue value2) {
        Double doubleValue1 = value1.extractValueWithExpectation(Double.class);
        Double doubleValue2 = value2.extractValueWithExpectation(Double.class);
        return doubleValue1.compareTo(doubleValue2);
    }

    private Map<Coordinate, Cell> updateCellsInSheet(Sheet copySheet, List<List<Cell>> sortedRows, Coordinate from, Coordinate to) {

        Map<Coordinate, Cell> newActiveCells = new HashMap<>();

        Map<Coordinate,String> newBackgroundColors= new HashMap<>();
        Map<Coordinate,String> newTextColors = new HashMap<>();

        for (int row = from.getRow(); row <= to.getRow(); row++) {
            List<Cell> sortedRow = sortedRows.get(row - from.getRow());

            for (int col = from.getColumn(); col <= to.getColumn(); col++) {

                Cell cell = sortedRow.get(col - from.getColumn());

                if (!cell.getOriginalValue().equals(String.valueOf(Double.MAX_VALUE))) {
                    Coordinate newCoord = CoordinateFactory.createCoordinate(row, col);
                    String textColor= copySheet.getTextColors().get(cell.getCoordinate());
                    String backgroundColor= copySheet.getBackgroundColors().get(cell.getCoordinate());
                    if( textColor!= null){
                        newTextColors.put(newCoord, textColor);
                        copySheet.getTextColors().remove(cell.getCoordinate());
                    }
                    if( backgroundColor!= null){
                        newBackgroundColors.put(newCoord, backgroundColor);
                        copySheet.getBackgroundColors().remove(cell.getCoordinate());
                    }
                    copySheet.getActiveCellMap().remove(cell.getCoordinate());
                    cell.setCoordinate(newCoord);
                    newActiveCells.put(newCoord, cell);
                }
            }
        }
        copySheet.getActiveCellMap().forEach(newActiveCells::put);
        copySheet.getBackgroundColors().forEach(newBackgroundColors::put);
        copySheet.getTextColors().forEach(newTextColors::put);
        copySheet.setTextColors(newTextColors);
        copySheet.setBackgroundColors(newBackgroundColors);
        return newActiveCells;
    }
}
