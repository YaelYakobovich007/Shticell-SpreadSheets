package filtering;

import range.Boundaries;
import sheet.api.Sheet;
import sheet.cell.api.Cell;
import sheet.coordinate.Coordinate;
import sheet.coordinate.CoordinateFactory;
import java.util.*;

public class SheetFilter {
    private final Map<Character, List<String>> selectedColumns2UniqueValues;
    private final Sheet sheet;
    private final Boundaries boundaries;
    private final List<List<Cell>> deleteRows= new ArrayList<>();

    public SheetFilter(Sheet sheet, Map<Character, List<String>> selectedColumns2UniqueValues, Boundaries boundaries){
        this.sheet = sheet;
        this.selectedColumns2UniqueValues = selectedColumns2UniqueValues;
        this.boundaries = boundaries;
    }

    public Sheet filter() {
        List<List<Cell>> collectRows = collectRows(boundaries.getFrom(), boundaries.getTo());
        Iterator<List<Cell>> rowIterator = collectRows.iterator();
        while (rowIterator.hasNext()) {
            List<Cell> row = rowIterator.next();
            if (!filterRow(row)) {
                rowIterator.remove();
                deleteRows.add(row);
            }
        }
        updateCellsInSheet(collectRows,boundaries.getFrom(), boundaries.getTo());

        return sheet;
    }

    private List<List<Cell>> collectRows(Coordinate from, Coordinate to) {
        List<List<Cell>> rows = new ArrayList<>();
        for (int row = from.getRow(); row <= to.getRow(); row++) {
            List<Cell> rowCells = new ArrayList<>();
            for (int col = from.getColumn(); col <= to.getColumn(); col++) {
                Cell cell = sheet.getCell(row, col);
                rowCells.add(cell);
            }
            rows.add(rowCells);
        }

        return rows;
    }


    private boolean filterRow(List<Cell> row) {
        for (char col : selectedColumns2UniqueValues.keySet()) {
            int colIndex = getColumnIndex(col);
            List<String> values = selectedColumns2UniqueValues.get(col);

            Cell cell = row.get(colIndex);
            if (cell != null) {
                if (!values.contains(cell.getEffectiveValue().toString())) {
                    return false;
                }
            }
            else{
                return false;
            }
        }
        return true;
    }

    private int getColumnIndex(char col) {
        return (col - 'A' + 1) - boundaries.getFrom().getColumn();
    }

    private void updateCellsInSheet(List<List<Cell>> sortedRows, Coordinate from, Coordinate to) {

        Map<Coordinate, Cell> newActiveCells = new HashMap<>();
        Map<Coordinate,String> newBackgroundColors= new HashMap<>();
        Map<Coordinate,String> newTextColors = new HashMap<>();

        int fromRow = from.getRow();

        for (int row = 0; row < sortedRows.size(); row++) {
            List<Cell> sortedRow = sortedRows.get(row);

            for (int col = from.getColumn(); col <= to.getColumn(); col++) {

                Cell cell = sortedRow.get(col - from.getColumn());

                if (cell!= null) {
                    Coordinate newCoord = CoordinateFactory.createCoordinate(fromRow, col);


                    String textColor= sheet.getTextColors().get(cell.getCoordinate());
                    String backgroundColor= sheet.getBackgroundColors().get(cell.getCoordinate());

                    if( textColor!= null){
                        newTextColors.put(newCoord, textColor);
                        sheet.getTextColors().remove(cell.getCoordinate());
                    }
                    if( backgroundColor!= null){
                        newBackgroundColors.put(newCoord, backgroundColor);
                        sheet.getBackgroundColors().remove(cell.getCoordinate());
                    }

                    sheet.getActiveCellMap().remove(cell.getCoordinate());

                    cell.setCoordinate(newCoord);

                    newActiveCells.put(newCoord, cell);
                }
            }
            fromRow++;
        }
        for(List<Cell> row : deleteRows) {
            for (Cell cell : row) {
                if(cell!= null){
                    sheet.getActiveCellMap().remove(cell.getCoordinate());
                    sheet.getBackgroundColors().remove(cell.getCoordinate());
                    sheet.getTextColors().remove(cell.getCoordinate());
                }
            }
        }
        newActiveCells.putAll(sheet.getActiveCellMap());
        newBackgroundColors.putAll(sheet.getBackgroundColors());
        newTextColors.putAll(sheet.getTextColors());

        sheet.setTextColors(newTextColors);
        sheet.setBackgroundColors(newBackgroundColors);
        sheet.setActiveCell(newActiveCells);

    }
}



