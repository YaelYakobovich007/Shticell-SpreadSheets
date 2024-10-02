package sheet.api;

import java.io.Serializable;

public interface Layout extends Serializable {
    int getNumOfRows();
    int getNumOfColumns();
    CellSize getLayoutSize();
}
