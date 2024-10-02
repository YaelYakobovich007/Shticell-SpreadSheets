package sheet.api;

import java.io.Serializable;

public interface CellSize extends Serializable {
    int getRowHeightUnit();
    int getColumnWidthUnit();
}
