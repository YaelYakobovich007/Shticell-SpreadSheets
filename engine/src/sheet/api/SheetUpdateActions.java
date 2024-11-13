package sheet.api;

import range.Boundaries;
import sheet.cell.api.Cell;
import sheet.coordinate.Coordinate;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

public interface SheetUpdateActions extends Serializable {
    Sheet updateCellValueAndCalculate(int row, int column, String value,String updatedByUser);
    void increaseVersion();
    void initializeCell(int row, int column, String value);
    Sheet createNewRange(String name, String from, String to);
    Sheet deleteRange(String name);
    Sheet rowSorting(Coordinate from, Coordinate to, List<Character> selectedColumns);
    void setActiveCell(Map<Coordinate, Cell> newActiveCell);
    void setBackgroundColor(Coordinate coordinate, String color);
    void setTextColor(Coordinate coordinate, String color);
    void  setTextColors(Map<Coordinate, String> newTextColors);
    void  setBackgroundColors(Map<Coordinate, String> newBackgroundColors);
    Sheet filtering(Boundaries boundaries, Map<Character,List<String>> selectedColumns2UniqueValues);
    Sheet updateTemporaryCellValue(int row, int column, String value);
}
