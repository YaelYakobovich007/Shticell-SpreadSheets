package sheet.version.api;

import sheet.api.Sheet;
import java.io.Serializable;

public interface Version  extends Serializable {
    int getVersionNumber();
    int getChangedCellsCount();
    Sheet getSheet();
    void updateSheet(Sheet sheet);
}

