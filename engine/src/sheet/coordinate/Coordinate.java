package sheet.coordinate;

import java.io.Serializable;

public interface Coordinate extends Serializable {
    int getRow();
    int getColumn();
}
