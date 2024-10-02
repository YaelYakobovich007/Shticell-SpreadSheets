package sheet.api;

import range.Range;
import sheet.cell.api.Cell;
import sheet.cell.api.EffectiveValue;
import sheet.coordinate.Coordinate;
import java.io.Serializable;
import java.util.Map;
import java.util.Set;

public interface SheetReadActions extends Serializable {
    int getVersionNumber();
    int getChangedCellsCount();
    Cell getCell(int row, int column);
    Layout getLayout();
    Map<Coordinate,Cell> getActiveCellMap();
    String getName();
     Range getRange(String name);
     Map<String,Range> getRanges();
     Map<Coordinate, String> getBackgroundColors();
     Map<Coordinate, String> getTextColors();
     Set<EffectiveValue> getUniqueValuesForColumn(Character column,Coordinate from,Coordinate to);
    void addNewRangeInUse(String rangeName);
}
